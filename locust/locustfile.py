from locust import HttpUser, task, between, events, LoadTestShape
import uuid
import logging
import requests
from datetime import datetime, timedelta


@events.test_start.add_listener
def on_start(environment, **kwargs):
    # test 시작시점에 학습할 lecture를 생성한다 
    logging.info("init setting")
    try: 
        response = requests.post(
            environment.host + "/lectures", json={"name": "test_lecture", "capacity" : 30}
        )
        Student.lectureId = response.json()["lectureId"]
        logging.info(f"Lecture생성됨 {Student.lectureId}")
    except Exception as ex: 
        logging.warning(ex)
        environment.runner.quit()

    # 수강신청 가능기간을 설정한다 
    try: 
        now = datetime.now()
        response = requests.post(
            environment.host + "/registrations/periods", json={"startTime": (now + timedelta(seconds=120)).isoformat(), "endTime" : (now + timedelta(seconds=240)).isoformat()}
        )
        logging.info(f"수강신청시간 생성됨")
    except Exception as ex: 
        logging.warning(ex)
        environment.runner.quit()

class Student(HttpUser):
    wait_time = between(0.5, 1.5)
    lectureId = None

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.accessToken = None
        self.retry = 0

    def on_start(self): 
        with self.client.post(
            "/signup",
            json={
                'username' : f"student_{uuid.uuid4()}",
                'password' : 'password'
            },
            catch_response=True
        ) as response: 
            if response.status_code == 201:
                data = response.json()
                if data is not None and 'accessToken' in data:
                    self.accessToken = data['accessToken']
                    response.success()
                else:
                    data = response.json()
                    response.failure(f'[No Token] Type: {data['type']} detail : {data['detail']}')
            else: 
                data = response.json()
                response.failure(f'[Fail] Type: {data['type']} detail : {data['detail']}')

    @task
    def regist(self):
        if not self.accessToken:
            self.stop() # 인증실패시 중단
            return
        with self.client.post(
            '/registrations',
            json={
                'lectureId' : Student.lectureId
            },
            headers={
                'Authorization' : f'Bearer {self.accessToken}'
            },
            catch_response=True
        ) as response:
            if response.status_code == 201:
                data = response.json()  
                response.success()
                self.stop() # 등록 성공시 중단
            elif response.status_code == 401: # 인증실패
                data = response.json()  
                response.failure(f'[Authentication Fail]  Type: {data['type']} detail : {data['detail']}')
            elif response.status_code == 409: # 꽉참
                data = response.json()  
                response.failure(f'[Lectuer is Full]  Type: {data['type']} detail : {data['detail']}')
                self.retry+=1
                if self.retry >= 3:
                    self.stop() # 꽉차면 중단 
            else: 
                data = response.json()
                response.failure(f'[Fail] Type: {data['type']} detail : {data['detail']}')

class RegistrationShape(LoadTestShape):
    spike_max_users = 500 # 수강신청 시점의 최대 사용자수 
    time_to_spike = 120 # 수강신청까지 걸리는 시간 
    spike_duration = 240 # 수강신청 이후 언제까지 유지될 것인가

    stages = [
        # 30초 전까지 천천히 증가 
        {"duration": time_to_spike-30, "users": spike_max_users//4, "spawn_rate": 5, "user_classes": [Student]}, 
        # 30초전부터 급격히 증가 
        {"duration": time_to_spike, "users": spike_max_users, "spawn_rate": 50, "user_classes": [Student]},
        # 이후 2분간 수강신청 시도 
        {"duration": time_to_spike+spike_duration, "users": spike_max_users, "spawn_rate": 0, "user_classes": [Student]},
    ]

    def tick(self):
        run_time = self.get_run_time()

        for stage in self.stages:
            if run_time < stage["duration"]:
                try:
                    tick_data = (stage["users"], stage["spawn_rate"], stage["user_classes"])
                except:
                    tick_data = (stage["users"], stage["spawn_rate"])
                return tick_data

        # return None은 테스트를 종료시킨다 
        return None


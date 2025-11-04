from locust import HttpUser, task, between, events
import uuid
import logging
import gevent
import time
from threading import Barrier

barrier = None

@events.test_start.add_listener
def on_start(environment, **kwargs):
    global barrier
    barrier = Barrier(environment.runner.target_user_count)

# @events.spawning_complete.add_listener
# def spawning_complete(user_count):
#     logging.info(f"All user setting complete {user_count}")
#     time.sleep(60)
#     logging.info(f"registration start")
#     Student.all_user_ready.set()


class Student(HttpUser):
    # all_user_ready = gevent.event.Event()
    wait_time = between(0.1,0.3)

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.accessToken = None

    def on_start(self): 
        with self.client.post(
            "/signup",
            json={
                'username' : f"student_{uuid.uuid4()}",
                'password' : 'password'
            },
            catch_response=True
        ) as response: 
            data = response.json()
            if response.status_code == 201:
                if 'body' in data and data['body'] is not None and 'accessToken' in data['body']:
                    self.accessToken = data['body']['accessToken']
                    response.success()
                    logging.info('사용자 등록완료')
                else:
                    response.failure(f'[No Token] Status: {data['header']['status']} code : {data['header']['message']}')
            else: 
                response.failure(f'[Fail] Status: {data['header']['status']} code : {data['header']['message']}')

    @task
    def regist(self):
        barrier.wait()
        logging.info("수강신청시작")
        # Student.all_user_ready.wait()
        if not self.accessToken:
            self.stop() # 인증실패시 중단
            return
        with self.client.post(
            '/registrations',
            json={
                'lectureId' : 20150726
            },
            headers={
                'Authorization' : f'Bearer {self.accessToken}'
            },
            catch_response=True
        ) as response:
            data = response.json()  
            if response.status_code == 201:
                response.success()
                self.stop() # 등록 성공시 중단
            elif response.status_code == 401:
                response.failure(f'[Authentication Fail]  Status: {data['header']['status']} code : {data['header']['message']}')
            elif response.status_code == 409: 
                response.failure(f'[Lectuer is Full]  Status: {data['header']['status']} code : {data['header']['message']}')
                self.stop() # 꽉차면 중단 
            else: 
                response.failure(f'[Fail] Status: {data['header']['status']} code : {data['header']['message']}')

"use client"

import {useState} from "react";
import {CreatePeriodRequest} from "@/src/type/registration/period/period";
import {Card} from "@/src/components/Card/Card";
import {Button} from "@/src/components/button/Button";
import {DatetimeInput} from "@/src/components/datetime/DatetimeInput";

export default function RegistrationPeriodPage(){
    const now = new Date();
    const [startTime, setStartTime] = useState<Date>(new Date());
    const [endTime, setEndTime] = useState<Date>(now);

    const onSubmit = (e) => {
        e.preventDefault();

        const requestBody : CreatePeriodRequest = {
            startTime: startTime.toISOString(),
            endTime: endTime.toISOString()
        }

        fetch('/api/registrations/periods', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });
    }
    
    const formatTime = (date: Date) => {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const hour = date.getHours();
        const minute = date.getMinutes();
        return `${year}년 ${month}월 ${day}일 ${hour}시 ${minute}분`
    }

    return (
        <div>
            <Card>
                <h2 className={"text-center text-lg font-bold text-xl mb-2"}>
                    수강신청기한 생성
                </h2>
                <form onSubmit={onSubmit}>
                    <div className={"block mt-5"}>
                        <label className={"mr-5"}>
                            시작시간
                        </label>
                        <DatetimeInput
                            value={startTime}
                            onChange={setStartTime}
                        />
                    </div>
                    <div className={"block mt-5"}>
                        <label className={"mr-5"}>
                            종료시간
                        </label>
                        <DatetimeInput
                            value={endTime}
                            onChange={setEndTime}
                        />
                        <p>

                        </p>
                    </div>
                    <div className={"flex mt-5"}>
                        <p className={"text-center py-3"}>
                            <span className={"font-semibold"}>
                                {formatTime(startTime)}
                            </span>
                            <span className={"mx-2"}>
                                부터
                            </span>
                            <span className={"font-semibold"}>
                                {formatTime(endTime)}
                            </span>
                            <span className={"mx-2"}>
                                까지
                            </span>
                        </p>
                        <Button type="submit" className={"bg-blue-50 hover:bg-blue-400 hover:text-white"}>
                            제출하기
                        </Button>
                    </div>
                </form>
            </Card>
        </div>
    )
}
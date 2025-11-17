"use client"

import {useState} from "react";
import {CreatePeriodRequest} from "@/src/type/registration/period/period";

export default function RegistrationPeriodPage(){
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');

    const onSubmit = (e) => {
        e.preventDefault();

        const requestBody : CreatePeriodRequest = {
            startTime: startTime,
            endTime: endTime
        }

        fetch('/api/registrations/periods', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });
    }

    return (
        <div>
            <form onSubmit={onSubmit}>
                <input type="datetime-local"
                       value={startTime}
                       onChange={(e) => setStartTime(e.target.value)}
                />
                <input type="datetime-local"
                       value={endTime}
                       onChange={(e) => setEndTime(e.target.value)}
                />
                <button type="submit">
                    제출하기
                </button>
            </form>
        </div>
    )
}
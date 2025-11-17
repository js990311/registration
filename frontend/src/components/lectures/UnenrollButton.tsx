import {useState} from "react";
import {ProblemResponse} from "@/src/type/error/error";

type UnenrollButtonPageProps = {
    registrationId: number,
    onUnenroll: (registrationId: number) => void,
}

export default function UnenrollButton({registrationId, onUnenroll}: Readonly<UnenrollButtonPageProps>) {
    const [message, setMessage] = useState<string>();

    const onClickUnenroll =  async () => {

        const response = await fetch(`/api/registrations/${registrationId}`, {
            method: "DELETE",
        });

        if(response.status === 204) {
            onUnenroll(registrationId);
        }else{
            const problem : ProblemResponse = await response.json();
            setMessage(problem.detail);
        }
    }

    return (
        <div>
            <button onClick={onClickUnenroll}>
                수강 취소
            </button>
            <div>
                {message}
            </div>
        </div>
    )
}
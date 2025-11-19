import {useState} from "react";
import {CreateRegistrationRequest} from "@/src/type/registration/registration";
import {ProblemResponse} from "@/src/type/error/error";

export const useEnroll = ()=> {
    const [error, setError] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);
    const [success, setSuccess] = useState<boolean>(false);

    const onEnroll =  async (lectureId: number) => {
        if(loading){
            return
        }

        try {
            setLoading(true);
            setSuccess(false);
            setError('');

            const request: CreateRegistrationRequest = {
                lectureId: lectureId,
            };

            const response = await fetch(`/api/registrations`, {
                method: "POST",
                body: JSON.stringify(request),
            });

            if(response.status === 201) {
                setSuccess(true);
            }else{
                const problem : ProblemResponse = await response.json();
                setError(`${problem.type} : ${problem.detail}`);
            }

        }catch (error) {
            const errorMsg = `FRONTEND ERROR: ${error instanceof Error ? error.message : String(error)}`;
            console.log(errorMsg);
            setError(errorMsg);
        }finally {
            setLoading(false);
        }
    }
    return {
        error, success, loading,
        onEnroll
    }
}


export const useUnenroll = (onUnenrollHandler: (registrationId: number) => void)=> {
    const [error, setError] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);
    const [success, setSuccess] = useState<boolean>(false);

    const fetchUnenroll =  async (registrationId: number) => {
        if(loading){
            return
        }

        try {
            setLoading(true);
            setSuccess(false);
            setError('');

            const response = await fetch(`/api/registrations/${registrationId}`, {
                method: "DELETE",
            });

            if(response.status === 204) {
                setSuccess(true);
                onUnenrollHandler(registrationId);
            }else{
                const problem : ProblemResponse = await response.json();
                setError(`${problem.type} : ${problem.detail}`);
            }

        }catch (error) {
            const errorMsg = `FRONTEND ERROR: ${error instanceof Error ? error.message : String(error)}`;
            console.log(errorMsg);
            setError(errorMsg);
        }finally {
            setLoading(false);
        }
    }
    return {
        error, success, loading,
        fetchUnenroll
    }
}
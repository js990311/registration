import {useEffect, useState} from "react";
import {ProblemResponse} from "@/src/type/error/error";
import {useUnenroll} from "@/src/hooks/registrationHook";
import toast from "react-hot-toast";
import {Button} from "@/src/components/button/Button";
import clsx from "clsx";
import styles from "./UnenrollButton.module.css"

type UnenrollButtonPageProps = {
    registrationId: number,
    onUnenroll: (registrationId: number) => void,
}

export default function UnenrollButton({registrationId, onUnenroll}: Readonly<UnenrollButtonPageProps>) {
     const {
         error, success, loading, fetchUnenroll
     } = useUnenroll(onUnenroll);

    const [message, setMessage] = useState<string>();

    useEffect(() => {
        if(!success && error) {
            toast.error(error);
        }else if(success){
            toast.success('수강 취소 성공');
        }
    }, [success, error]);

    return (
        <div>
            <Button
                className={clsx(styles.unenrollButton)}
                onClick={()=>fetchUnenroll(registrationId)}>
                수강 취소
            </Button>
        </div>
    )
}
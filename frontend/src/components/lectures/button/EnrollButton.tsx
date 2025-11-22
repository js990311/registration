"use client"

import {useEnroll} from "@/src/hooks/registrationHook";
import toast from "react-hot-toast";
import {useEffect} from "react";
import {Button} from "@/src/components/button/Button";
import styles from "./EnrollButton.module.css"
import clsx from "clsx";

type EnrollButtonPageProps = {
    lectureId: number,
    disabled : boolean,
}

export default function EnrollButton({lectureId, disabled} : Readonly<EnrollButtonPageProps>) {
    const {
        error, success, loading,
        onEnroll
    } = useEnroll();

    useEffect(() => {
        if(!success && error) {
            toast.error(error);
        }else if(success){
            toast.success('수강 신청 성공');
        }
    }, [success, loading, error]);

    return (
        <div>
            {
                disabled ? (
                    <Button
                        className={clsx(styles.closedButton)}
                        onClick={() => onEnroll(lectureId)}
                        disabled={disabled}
                    >
                        정원초과
                    </Button>
                ) : (
                    <Button
                        className={clsx(styles.enrollButton)}
                        onClick={() => onEnroll(lectureId)}
                        disabled={loading}
                    >
                        {loading ? '신청중' : '수강신청'}
                    </Button>
                )
            }
        </div>
    )
}
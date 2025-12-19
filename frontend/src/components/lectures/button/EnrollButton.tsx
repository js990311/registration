"use client"

import toast from "react-hot-toast";
import {useState} from "react";
import {Button} from "@/src/components/button/Button";
import styles from "./EnrollButton.module.css"
import clsx from "clsx";
import {CreateRegistrationRequest, CreateRegistrationResponse} from "@/src/type/registration/registration";
import {ActionOneResponse} from "@/src/type/response/actionResponse";
import {errorToString} from "@/src/lib/api/apiError";
import {registration} from "@/src/action/registrationAction";

type EnrollButtonPageProps = {
    lectureId: number,
    disabled : boolean,
}

export default function EnrollButton({lectureId, disabled} : Readonly<EnrollButtonPageProps>) {
    const [loading, setLoading] = useState<boolean>(false);

    const onEnroll = async (lectureId:number) => {
        setLoading(true);
        const request: CreateRegistrationRequest = {
            lectureId: lectureId,
        };

        registration(request)
            .then(resp => {
                setLoading(false);
                if(resp.success){
                    toast.success("수강신청 성공");
                }else {
                    toast.error(errorToString("", resp.error));
                }
            })
        ;
    }

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
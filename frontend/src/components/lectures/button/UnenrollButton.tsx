import toast from "react-hot-toast";
import {Button} from "@/src/components/button/Button";
import clsx from "clsx";
import styles from "./UnenrollButton.module.css"
import {errorToString} from "@/src/lib/api/apiError";

type UnenrollButtonPageProps = {
    registrationId: number,
    onUnenroll: (registrationId: number) => void,
}

export default function UnenrollButton({registrationId, onUnenroll}: Readonly<UnenrollButtonPageProps>) {

    const onCancel = async (registrationId: number)=>{
        const response = await fetch(`/api/registrations/${registrationId}`, {
            method: "DELETE",
        });
        if (response.ok) {
            toast.success('수강 신청 해제 성공');
            onUnenroll(registrationId);
        }else {
            const error = await response.json();
            toast.error(errorToString("", error));
        }
    }

    return (
        <div>
            <Button
                className={clsx(styles.unenrollButton)}
                onClick={()=>onCancel(registrationId)}>
                수강 취소
            </Button>
        </div>
    )
}
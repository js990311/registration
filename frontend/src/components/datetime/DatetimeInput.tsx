import {useState} from "react";
import DatePicker from "react-datepicker";
import 'react-datepicker/dist/react-datepicker.css';

interface DatetimeInputProps{
    value: Date;
    onChange: Function;
}

export const DatetimeInput = ({value, onChange} : DatetimeInputProps) => {

    return (
        <DatePicker
            selected={value}
            onChange={(date:Date | null) => {onChange(date)}}
            showTimeSelect
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            dateFormat={"YYYY년 MM월 dd일 HH시 mm분"}
        />
    );
}
"use client"

import styles from './pagination.module.css'
import clsx from "clsx";
import {Button} from "@/src/components/button/Button";
import { MdOutlineKeyboardDoubleArrowLeft, MdOutlineKeyboardDoubleArrowRight, MdOutlineKeyboardArrowLeft, MdOutlineKeyboardArrowRight  } from "react-icons/md";
import { HiDotsHorizontal } from "react-icons/hi";
import {useState} from "react";
import {PaginationInfo} from "@/src/type/response/pagination";

type PaginationControllerProps = {
    pagination : PaginationInfo,
    onHandlePage: (page : number, size: number) => void,
}

const PaginationPageInput = ({totalPage} : { totalPage:number }) => {
    const [isOpen, setIsOpen] = useState<boolean>(false);
    return (
        <li className={clsx(styles.paginationPageInputContainer, styles.paginationButton, {
            [styles.currentPage]: isOpen
        })}>
            <Button
                onClick={() => {setIsOpen(!isOpen)}}
            >
                <HiDotsHorizontal />
            </Button>
            <div className={clsx(
                styles.paginationPageInputDropout,
                {
                    [styles.dropdownOpen]: isOpen
                }
            )}
            >
                <input type="text" placeholder={`page : `}/>
                <span>{totalPage}</span>
                <Button>이동</Button>
            </div>
        </li>
    )
}

export default function PaginationController({pagination, onHandlePage}: Readonly<PaginationControllerProps>) {
    const minPage = pagination.blockLeft;
    const maxPage = pagination.blockRight;
    const size = pagination.requestSize;

    const pages: number[] = Array.from({ length: maxPage - minPage + 1 }).map((_, i) => minPage + i);

    return (
        <div className={clsx(styles.paginationContainer)}>
            <div>
                <select
                    className={clsx(styles.paginationSelect)}
                    name={"pagination-size"}
                    id="pagination-size"
                    value={pagination.requestSize}
                    onChange={(e) => {
                        const newSize = parseInt(e.target.value);
                        onHandlePage(1, newSize);
                    }}
                >
                    <option value="15">
                        15개 
                    </option>
                    <option value="30">
                        30개
                    </option>
                    <option value="50">
                        50개
                    </option>
                </select>
            </div>
            <ul className={clsx(styles.paginationList)}>
                <li>
                    <Button
                        className={styles.paginationButton}
                        onClick={()=>onHandlePage(1,size)}>
                        <MdOutlineKeyboardDoubleArrowLeft />
                    </Button>
                </li>
                <li>
                    <Button
                        className={styles.paginationButton}
                        onClick={()=>onHandlePage(Math.max(1, minPage-10),size)}>
                        <MdOutlineKeyboardArrowLeft />
                    </Button>
                </li>
                {
                    pages.map((page: number) => (
                        <li
                            key={`page-remote-${page}`}
                        >
                            <Button
                                className={clsx(styles.paginationButton, {
                                    [styles.currentPage]: page === pagination.requestNumber
                                })}
                                onClick={() => onHandlePage(page, size)}>
                                {page}
                            </Button>
                        </li>
                    ))
                }
                <PaginationPageInput
                    totalPage={pagination.totalPage}
                />
                <li>
                    <Button
                        className={styles.paginationButton}
                        onClick={() => onHandlePage(Math.min(pagination.totalPage, maxPage+1), size)}>
                        <MdOutlineKeyboardArrowRight />
                    </Button>
                </li>
                <li>
                    <Button
                        className={styles.paginationButton}
                        onClick={()=>onHandlePage(pagination.totalPage,size)}>
                        <MdOutlineKeyboardDoubleArrowRight />
                    </Button>
                </li>

            </ul>
        </div>
    );
}
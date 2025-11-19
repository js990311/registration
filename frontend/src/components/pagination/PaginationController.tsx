"use client"

import {Pagination} from "@/src/type/pagination/pagination";
import Link from "next/link";

type PaginationControllerProps = {
    pagination : Pagination<any>,
    onHandlePage: (page : number, size: number) => void,
}

export default function PaginationController({pagination, onHandlePage}: Readonly<PaginationControllerProps>) {
    const minPage = Math.floor((pagination.pageNumber-1) / 10) * 10 + 1;
    const maxPage = Math.min(pagination.totalPage, minPage+9);
    const size = pagination.pageSize;

    const pages: number[] = Array.from({ length: maxPage - minPage + 1 }).map((_, i) => minPage + i);



    return (
        <div>
            <div>
                size를 변경하는 input
                <select
                    name={"pagination-size"}
                    id="pagination-size"
                    value={pagination.pageSize}
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
            <ul>
                <li>
                    <button onClick={()=>onHandlePage(1,size)}>
                        맨처음페이지
                    </button>
                </li>
                <li>
                    <button onClick={()=>onHandlePage(Math.max(1, minPage-10),size)}>
                        이전페이지
                    </button>
                </li>
                {
                    pages.map((page: number) => (
                        <li
                            key={`page-remote-${page}`}
                            className={`${page === pagination.pageNumber ? "current-page" : ""}`}>
                            <button onClick={() => onHandlePage(page, size)}>
                                {page}
                            </button>
                        </li>
                    ))
                }
                <li>
                <button onClick={() => onHandlePage(Math.min(pagination.totalPage, maxPage+1), size)}>
                        다음 페이지
                    </button>
                </li>
                <li>
                    <button onClick={()=>onHandlePage(pagination.totalPage,size)}>
                        akwlakr vpdlwl
                    </button>
                </li>

            </ul>
        </div>
    );
}
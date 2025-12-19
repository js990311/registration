import {ExceptionDetail} from "@/src/type/error/exceptionDetail";

interface ErrorDisplayProps {
    error?: ExceptionDetail;
    fallbackMessage?: string;
}

export default function ErrorDisplay({ error, fallbackMessage }: ErrorDisplayProps) {
    return (
        <div className="p-6 border-2 border-red-100 rounded-lg bg-red-50">
            <h2 className="text-xl font-bold text-red-600 mb-2">
                {error?.status === 404 ? "데이터를 찾을 수 없습니다" : "오류가 발생했습니다"}
            </h2>
            <p className="text-gray-700">
                {error?.detail || fallbackMessage || "알 수 없는 에러가 발생했습니다."}
            </p>
            {process.env.NODE_ENV === 'development' && error && (
                <details className="mt-4 p-2 bg-gray-100 rounded text-xs font-mono">
                    <summary className="cursor-pointer mb-2">Error Details (Dev Only)</summary>
                    <pre>{JSON.stringify(error, null, 2)}</pre>
                </details>
            )}
        </div>
    );
}
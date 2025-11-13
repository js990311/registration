import {create} from "zustand/react";

type LoginStore = {
    isLogin: boolean;
    login: ()=>void,
    logout: ()=>void,
}

const useLoginStore = create<LoginStore>((set) => ({
        isLogin: false,

        login: () => set({isLogin: true}),
        logout: () => set({isLogin: false}),
    })
)

export default useLoginStore;
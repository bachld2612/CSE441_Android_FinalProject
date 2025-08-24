import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}


const changePassword = async (data: ChangePasswordRequest): Promise<ApiResponse<string>> => {

    try{
        const res: ApiResponse<string> = await api.post("/tai-khoan/doi-mat-khau", data);
        console.log("AuthService - changePassword response:", res);
        return res;
    }catch(error){
        console.error("AuthService - changePassword error:", error);
       throw error;
    }

}

export{
    changePassword
}
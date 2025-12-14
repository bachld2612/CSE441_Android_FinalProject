package com.bachld.project.backend.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Util {

    public boolean kiemTraThapPhan(double value){
        int actualPlaces = BigDecimal.valueOf(value).stripTrailingZeros().scale();
        return Math.max(0, actualPlaces) <= 3;
    }

    public boolean chuaSoVaKiTuDacBietHoacKhoangTrangOCuoi(String value){
        for(Character ch : value.toCharArray()){
            if(!Character.isLetter(ch) && !Character.isSpaceChar(ch)){
                return true;
            }
        }
        if(value.charAt(value.length()-1) == ' '){
            return true;
        }
        return false;
    }

    public boolean chuaSo(String value){
        for(Character ch : value.toCharArray()){
            if(Character.isDigit(ch)){
                return true;
            }
        }
        return false;
    }
}

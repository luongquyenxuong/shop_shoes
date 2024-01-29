package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.ReturnDetailDTO;
import com.example.shoes_shop.entity.ReturnDetail;

import java.util.HashSet;
import java.util.Set;

public class ReturnDetailMapper {

    public static Set<ReturnDetailDTO> convertToDTOSet(Set<ReturnDetail> returnDetailSet) {
        Set<ReturnDetailDTO> returnDetailDTOS = new HashSet<>();

        for (ReturnDetail returnDetail : returnDetailSet) {

            ReturnDetailDTO returnDetailDTO = new ReturnDetailDTO();
            returnDetailDTO.setReturn_id(returnDetail.getRefund().getId());
            returnDetailDTO.setProductId(returnDetail.getProduct().getId());
            returnDetailDTO.setId(returnDetail.getId());
            returnDetailDTO.setQuantityReturn(returnDetail.getQuantityReturn());
            returnDetailDTO.setQuantityOrder(returnDetail.getQuantityOrder());
            returnDetailDTO.setId(returnDetail.getId());
            returnDetailDTO.setSize(returnDetail.getSize());
            returnDetailDTO.setPrice(returnDetail.getPrice());
            returnDetailDTO.setTotalPrice(returnDetail.getPrice() * returnDetail.getQuantityReturn());
            returnDetailDTO.setProductImg(returnDetail.getProduct().getImages().get(0));
            returnDetailDTO.setProductName(returnDetail.getProduct().getName());
            returnDetailDTO.setProductSlug(returnDetail.getProduct().getSlug());
            returnDetailDTOS.add(returnDetailDTO);
        }


        return returnDetailDTOS;
    }

}

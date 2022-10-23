package com.goodspartner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private int id;
    private String userName;
    private String email;
    private String role;
    private boolean enabled = false;

}

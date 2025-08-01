package cn.com.traninfo.fastlcdp.erdesigner.entity;

@Data
@TableName("user")
public class UserEntity extends BaseEntity {

    @TableField("mobile_phone_number")
    private String mobilePhoneNumber;

    @TableField("email")
    private String email;

    @TableField("nickname")
    private String nickname;

    @TableField("head_img_url")
    private String headImgUrl;

}

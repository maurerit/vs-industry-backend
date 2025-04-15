package io.github.vaporsea.vsindustry.contract;

import lombok.Data;

@Data
public class EveJwtDTO {
    
    private String[] scp;
    private String jti;
    private String kid;
    private String sub;
    private String azp;
    private String tenant;
    private String tier;
    private String region;
    private String[] aud;
    private String name;
    private String owner;
    private Long exp;
    private Long iat;
    private String iss;
}

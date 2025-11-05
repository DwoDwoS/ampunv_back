package ampunv_back.dto;

public class PublicUserDTO {
    private Long id;
    private String displayName;
    private String cityName;
    private String memberSince;

    public PublicUserDTO(Long id, String firstname, String lastname, String cityName, String memberSince) {
        this.id = id;
        this.displayName = firstname + " " + lastname.charAt(0) + ".";
        this.cityName = cityName;
        this.memberSince = memberSince;
    }

    public Long getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getCityName() { return cityName; }
    public String getMemberSince() { return memberSince; }
}
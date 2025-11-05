package ampunv_back.dto;

public class PublicUserDTO {
    private final Long id;
    private final String displayName;
    private final String cityName;
    private final String memberSince;

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

    public void setId(Long id) {
    }

    public void setDisplayName(String displayName) {
    }

    public void setMemberSince(String s) {
    }

    public void setCityName(String name) {
    }
}
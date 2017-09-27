package br.unisinos.hefestos.provider;

public enum HefestosUriEnum {

    RESOURCE(100, HefestosContract.PATH_RESOURCE, HefestosContract.Resource.CONTENT_TYPE_ID, false, HefestosContract.Tables.RESOURCE),
    PTRAIL(200,HefestosContract.PATH_PTRAIL,HefestosContract.PTrail.CONTENT_TYPE_ID,false,HefestosContract.Tables.PTRAIL),
    PTRAIL_RESOURCE(300, HefestosContract.PATH_PTRAIL_RESOURCE, HefestosContract.PTrail.CONTENT_TYPE_ID, false, null),
    USER(400, HefestosContract.PATH_USER, HefestosContract.User.CONTENT_TYPE_ID, false, HefestosContract.Tables.USER),
    TAG(500, HefestosContract.PATH_TAG, HefestosContract.Tag.CONTENT_TYPE_ID, false, HefestosContract.Tables.TAG),
    RESOURCE_TAG(600, HefestosContract.PATH_RESOURCE_TAG, HefestosContract.Resource.CONTENT_TYPE_ID, false, null),;

    public int code;

    public String path;

    public String contentType;

    public String table;

    HefestosUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? HefestosContract.makeContentItemType(contentTypeId)
                : HefestosContract.makeContentType(contentTypeId);
        this.table = table;
    }
}

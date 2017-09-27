package br.unisinos.hefestos.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

public class HefestosProviderUriMatcher {

    /**
     * All methods on a {@link UriMatcher} are thread safe, except {@code addURI}.
     */
    private UriMatcher mUriMatcher;

    private SparseArray<HefestosUriEnum> mEnumsMap = new SparseArray<>();

    /**
     * This constructor needs to be called from a thread-safe method as it isn't thread-safe itself.
     */
    public HefestosProviderUriMatcher(){
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = HefestosContract.CONTENT_AUTHORITY;

        HefestosUriEnum[] uris = HefestosUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mUriMatcher.addURI(authority, uris[i].path, uris[i].code);
        }

        buildEnumsMap();
    }

    private void buildEnumsMap() {
        HefestosUriEnum[] uris = HefestosUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mEnumsMap.put(uris[i].code, uris[i]);
        }
    }

    /**
     * Matches a {@code uri} to a {@link HefestosUriEnum}.
     *
     * @return the {@link HefestosUriEnum}, or throws new UnsupportedOperationException if no match.
     */
    public HefestosUriEnum matchUri(Uri uri){
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e){
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    /**
     * Matches a {@code code} to a {@link HefestosUriEnum}.
     *
     * @return the {@link HefestosUriEnum}, or throws new UnsupportedOperationException if no match.
     */
    public HefestosUriEnum matchCode(int code){
        HefestosUriEnum HefestosUriEnum = mEnumsMap.get(code);
        if (HefestosUriEnum != null){
            return HefestosUriEnum;
        }
        throw new UnsupportedOperationException("Unknown uri with code " + code);
    }
}

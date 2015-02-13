package com.piotrrosa.fotoradaralert_ver3;

/**
 * Created by piotr on 13.02.15.
 */
public class Data {
    private boolean loadingStatus = false;

    public Data() {
        ;
    }
    public void setDataStatus(boolean status) {
        loadingStatus = status;
    }
    public boolean isLoading() {
        return loadingStatus;
    }
}

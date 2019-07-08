package com.batuhansubasi.akillireklamyonetimsistemi;

class Kampanyalar {

    private String FirmaAdi,KampanyaIcerik, KampanyaTuru;
    private String Enlem, Boylam;
    private String KampanyaSuresi;

    public Kampanyalar() {

    }
    public Kampanyalar(String firmaAdi, String kampanyaIcerik, String kampanyaTuru, String enlem, String boylam, String kampanyaSuresi) {
        FirmaAdi = firmaAdi;
        KampanyaIcerik = kampanyaIcerik;
        KampanyaTuru = kampanyaTuru;
        Enlem = enlem;
        Boylam = boylam;
        KampanyaSuresi = kampanyaSuresi;
    }

    public String getFirmaAdi() {
        return FirmaAdi;
    }

    public void setFirmaAdi(String firmaAdi) {
        FirmaAdi = firmaAdi;
    }

    public String getKampanyaIcerik() {
        return KampanyaIcerik;
    }

    public void setKampanyaIcerik(String kampanyaIcerik) {
        KampanyaIcerik = kampanyaIcerik;
    }

    public String getKampanyaTuru() {
        return KampanyaTuru;
    }

    public void setKampanyaTuru(String kampanyaTuru) {
        KampanyaTuru = kampanyaTuru;
    }

    public String getEnlem() {
        return Enlem;
    }

    public void setEnlem(String enlem) {
        Enlem = enlem;
    }

    public String getBoylam() {
        return Boylam;
    }

    public void setBoylam(String boylam) {
        Boylam = boylam;
    }

    public String getKampanyaSuresi() {
        return KampanyaSuresi;
    }

    public void setKampanyaSuresi(String kampanyaSuresi) {
        KampanyaSuresi = kampanyaSuresi;
    }
}

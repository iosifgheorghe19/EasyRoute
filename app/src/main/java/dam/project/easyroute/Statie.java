package dam.project.easyroute;

import java.util.ArrayList;

public class Statie {

    private int nid;
    private String numeStatie;
    private double longitudine;
    private double latitudine;
    private TipTransport tipStatie;
    private boolean activa;
    private ArrayList<String> listaMijloaceDeTransport;
    private boolean favorita;

    public Statie() {
        favorita = false;
    }
    public Statie(boolean activa, TipTransport tipStatie, int nid, double longitudine, double latitudine, String numeStatie, ArrayList<String> listaMijloaceDeTransport) {
        this.activa = activa;
        this.tipStatie = tipStatie;
        this.nid = nid;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.numeStatie = numeStatie;
        this.listaMijloaceDeTransport = listaMijloaceDeTransport;
    }
    public Statie(boolean activa, TipTransport tipStatie, int nid, double longitudine, double latitudine, String numeStatie, ArrayList<String> listaMijloaceDeTransport, boolean favorita) {
        this.activa = activa;
        this.tipStatie = tipStatie;
        this.nid = nid;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.numeStatie = numeStatie;
        this.listaMijloaceDeTransport = listaMijloaceDeTransport;
    }



    public double getLongitudine() {
        return longitudine;
    }
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }
    public double getLatitudine() {
        return latitudine;
    }
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }
    public int getNid() {
        return nid;
    }
    public void setNid(int nid) {
        this.nid = nid;
    }
    public String getNumeStatie() {
        return numeStatie;
    }
    public void setNumeStatie(String numeStatie) {
        this.numeStatie = numeStatie;
    }
    public TipTransport getTipStatie() {
        return tipStatie;
    }
    public void setTipStatie(TipTransport tipStatie) {
        this.tipStatie = tipStatie;
    }
    public boolean isActiva() {
        return activa;
    }
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    public ArrayList<String> getListaMijloaceDeTransport() {
        return listaMijloaceDeTransport;
    }
    public void setListaMijloaceDeTransport(ArrayList<String> listaMijloaceDeTransport) {
        this.listaMijloaceDeTransport = listaMijloaceDeTransport;
    }
    public boolean isFavorita() {
        return favorita;
    }
    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }
}

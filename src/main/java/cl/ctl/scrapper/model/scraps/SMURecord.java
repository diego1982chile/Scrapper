package cl.ctl.scrapper.model.scraps;

/**
 * Created by root on 06-09-21.
 */
public class SMURecord extends AbstractRegister {

    String periodo;
    String codUnimcarc;
    String codProveedor;
    String descripcionProducto;
    String marca;
    String estadoProducto;
    String unidadMedidaBase;
    String codLocal;
    String descripcionLocal;
    String estadoLocal;
    String formato;
    String tipo;
    double vtaUnid;
    double vtaPub;
    double vtaCosto;
    double inventario;
    double invACosto;

    public SMURecord() {
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCodUnimcarc() {
        return codUnimcarc;
    }

    public void setCodUnimcarc(String codUnimcarc) {
        this.codUnimcarc = codUnimcarc;
    }

    public String getCodProveedor() {
        return codProveedor;
    }

    public void setCodProveedor(String codProveedor) {
        this.codProveedor = codProveedor;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public String getUnidadMedidaBase() {
        return unidadMedidaBase;
    }

    public void setUnidadMedidaBase(String unidadMedidaBase) {
        this.unidadMedidaBase = unidadMedidaBase;
    }

    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
    }

    public String getDescripcionLocal() {
        return descripcionLocal;
    }

    public void setDescripcionLocal(String descripcionLocal) {
        this.descripcionLocal = descripcionLocal;
    }

    public String getEstadoLocal() {
        return estadoLocal;
    }

    public void setEstadoLocal(String estadoLocal) {
        this.estadoLocal = estadoLocal;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getVtaUnid() {
        return vtaUnid;
    }

    public void setVtaUnid(double vtaUnid) {
        this.vtaUnid = vtaUnid;
    }

    public double getVtaPub() {
        return vtaPub;
    }

    public void setVtaPub(double vtaPub) {
        this.vtaPub = vtaPub;
    }

    public double getVtaCosto() {
        return vtaCosto;
    }

    public void setVtaCosto(double vtaCosto) {
        this.vtaCosto = vtaCosto;
    }

    public double getInventario() {
        return inventario;
    }

    public void setInventario(double inventario) {
        this.inventario = inventario;
    }

    public double getInvACosto() {
        return invACosto;
    }

    public void setInvACosto(double invACosto) {
        this.invACosto = invACosto;
    }
}

package com.grupodasana.service.old;

import java.io.Serializable;
//import java.sql.Date;
import java.util.Date;

public class UsuarioPHP implements Serializable {
	private static final long serialVersionUID = -6097267698984041896L;

	public int id;
	public String name;
	public String post_slug;
	public String email;
	public String provider;
	public String country;
	public String nombre;
	public String apellidos;
	public Date nacimiento;
	public String estado;
	public String ciudad;
	public int comojupiste;
	public int blog_user;
	public String photo;
	public int admin;
	public Object auxiliar_admin;
	public String delete_status;
	public String codigo_referencia;
	public int id_tipo_usuario;
	public String created_at;
	public String updated_at;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPost_slug() {
		return post_slug;
	}

	public void setPost_slug(String post_slug) {
		this.post_slug = post_slug;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Date getNacimiento() {
		return nacimiento;
	}

	public void setNacimiento(Date nacimiento) {
		this.nacimiento = nacimiento;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public int getComojupiste() {
		return comojupiste;
	}

	public void setComojupiste(int comojupiste) {
		this.comojupiste = comojupiste;
	}

	public int getBlog_user() {
		return blog_user;
	}

	public void setBlog_user(int blog_user) {
		this.blog_user = blog_user;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}

	public Object getAuxiliar_admin() {
		return auxiliar_admin;
	}

	public void setAuxiliar_admin(Object auxiliar_admin) {
		this.auxiliar_admin = auxiliar_admin;
	}

	public String getDelete_status() {
		return delete_status;
	}

	public void setDelete_status(String delete_status) {
		this.delete_status = delete_status;
	}

	public String getCodigo_referencia() {
		return codigo_referencia;
	}

	public void setCodigo_referencia(String codigo_referencia) {
		this.codigo_referencia = codigo_referencia;
	}

	public int getId_tipo_usuario() {
		return id_tipo_usuario;
	}

	public void setId_tipo_usuario(int id_tipo_usuario) {
		this.id_tipo_usuario = id_tipo_usuario;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

}

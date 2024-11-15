package com.example.lab9_20220270.Dao;

import com.example.lab9_20220270.Bean.Estadio;
import com.example.lab9_20220270.Bean.Seleccion;

import java.sql.*;
import java.util.ArrayList;

public class DaoSelecciones extends DaoBase{
    public ArrayList<Seleccion> listarSelecciones() {

        ArrayList<Seleccion> selecciones = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM seleccion s " +
                     "LEFT JOIN estadio e ON s.estadio_idEstadio=e.idEstadio")) {

            while (rs.next()) {
                Seleccion seleccion = new Seleccion();

                seleccion.setIdSeleccion(rs.getInt("idSeleccion"));
                seleccion.setNombre(rs.getString("nombre"));
                seleccion.setTecnico(rs.getString("tecnico"));

                Estadio estadio = new Estadio();
                estadio.setIdEstadio(rs.getInt("idEstadio"));
                estadio.setNombre(rs.getString("nombre"));
                estadio.setProvincia(rs.getString("provincia"));
                estadio.setClub(rs.getString("club"));

                seleccion.setEstadio(estadio);

                selecciones.add(seleccion);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

        return selecciones;
    }
    public Seleccion buscarSeleccion(int idSeleccion) {
        Seleccion seleccion = null;
        String sql = "SELECT s.*, e.idEstadio, e.nombre AS nombreEstadio, e.provincia, e.club " +
                "FROM seleccion s " +
                "LEFT JOIN estadio e ON s.estadio_idEstadio = e.idEstadio " +
                "WHERE s.idSeleccion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSeleccion);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    seleccion = new Seleccion();
                    seleccion.setIdSeleccion(rs.getInt("idSeleccion"));
                    seleccion.setNombre(rs.getString("nombre"));
                    seleccion.setTecnico(rs.getString("tecnico"));

                    Estadio estadio = new Estadio();
                    estadio.setIdEstadio(rs.getInt("idEstadio"));
                    estadio.setNombre(rs.getString("nombreEstadio"));
                    estadio.setProvincia(rs.getString("provincia"));
                    estadio.setClub(rs.getString("club"));

                    seleccion.setEstadio(estadio);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seleccion;
    }
}

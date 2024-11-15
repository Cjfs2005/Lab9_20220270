package com.example.lab9_20220270.Dao;

import com.example.lab9_20220270.Bean.Arbitro;
import com.example.lab9_20220270.Bean.Estadio;
import com.example.lab9_20220270.Bean.Partido;
import com.example.lab9_20220270.Bean.Seleccion;

import java.sql.*;
import java.util.ArrayList;

public class DaoPartidos extends DaoBase {
    public ArrayList<Partido> listaDePartidos() {

        ArrayList<Partido> partidos = new ArrayList<>();

        String sql = "SELECT p.*, sL.idSeleccion AS idSeleccionLocal, sL.nombre AS nombreSeleccionLocal, sL.tecnico AS tecnicoLocal, " +
                "eL.idEstadio AS idEstadioLocal, eL.nombre AS nombreEstadioLocal, eL.provincia AS provinciaLocal, eL.club AS clubLocal, " +
                "sV.idSeleccion AS idSeleccionVisitante, sV.nombre AS nombreSeleccionVisitante, sV.tecnico AS tecnicoVisitante, " +
                "eV.idEstadio AS idEstadioVisitante, eV.nombre AS nombreEstadioVisitante, eV.provincia AS provinciaVisitante, eV.club AS clubVisitante, " +
                "a.* " +
                "FROM partido p " +
                "LEFT JOIN seleccion sL ON p.seleccionLocal = sL.idSeleccion " +
                "LEFT JOIN seleccion sV ON p.seleccionVisitante = sV.idSeleccion " +
                "LEFT JOIN arbitro a ON p.arbitro = a.idArbitro " +
                "LEFT JOIN estadio eL ON sL.estadio_idEstadio = eL.idEstadio " +
                "LEFT JOIN estadio eV ON sV.estadio_idEstadio = eV.idEstadio " +
                "ORDER BY p.idPartido;";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Partido partido = fetchPartidoData(rs);
                partidos.add(partido);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return partidos;
    }

    private Partido fetchPartidoData(ResultSet rs) throws SQLException {
        Partido partido = new Partido();

        partido.setIdPartido(rs.getInt("idPartido"));
        partido.setFecha(rs.getString("fecha"));
        partido.setNumeroJornada(rs.getInt("numeroJornada"));

        Seleccion seleccionLocal = new Seleccion();
        seleccionLocal.setIdSeleccion(rs.getInt("idSeleccionLocal"));
        seleccionLocal.setNombre(rs.getString("nombreSeleccionLocal"));
        seleccionLocal.setTecnico(rs.getString("tecnicoLocal"));

        Estadio estadioLocal = new Estadio();
        estadioLocal.setIdEstadio(rs.getInt("idEstadioLocal"));
        estadioLocal.setNombre(rs.getString("nombreEstadioLocal"));
        estadioLocal.setProvincia(rs.getString("provinciaLocal"));
        estadioLocal.setClub(rs.getString("clubLocal"));

        seleccionLocal.setEstadio(estadioLocal);
        partido.setSeleccionLocal(seleccionLocal);

        Seleccion seleccionVisitante = new Seleccion();
        seleccionVisitante.setIdSeleccion(rs.getInt("idSeleccionVisitante"));
        seleccionVisitante.setNombre(rs.getString("nombreSeleccionVisitante"));
        seleccionVisitante.setTecnico(rs.getString("tecnicoVisitante"));

        Estadio estadioVisitante = new Estadio();
        estadioVisitante.setIdEstadio(rs.getInt("idEstadioVisitante"));
        estadioVisitante.setNombre(rs.getString("nombreEstadioVisitante"));
        estadioVisitante.setProvincia(rs.getString("provinciaVisitante"));
        estadioVisitante.setClub(rs.getString("clubVisitante"));

        seleccionVisitante.setEstadio(estadioVisitante);
        partido.setSeleccionVisitante(seleccionVisitante);

        Arbitro arbitro = new Arbitro();
        arbitro.setIdArbitro(rs.getInt("idArbitro"));
        arbitro.setNombre(rs.getString("nombre"));
        arbitro.setPais(rs.getString("pais"));

        partido.setArbitro(arbitro);

        return partido;
    }

    public void crearPartido(Partido partido) {
        String sql = "INSERT INTO partido (seleccionLocal, seleccionVisitante, arbitro, fecha, numeroJornada) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, partido.getSeleccionLocal().getIdSeleccion());
            pstmt.setInt(2, partido.getSeleccionVisitante().getIdSeleccion());
            pstmt.setInt(3, partido.getArbitro().getIdArbitro());
            pstmt.setString(4, partido.getFecha());
            pstmt.setInt(5, partido.getNumeroJornada());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
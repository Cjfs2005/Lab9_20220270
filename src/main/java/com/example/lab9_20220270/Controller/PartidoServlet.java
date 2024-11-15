package com.example.lab9_20220270.Controller;

import com.example.lab9_20220270.Bean.Partido;
import com.example.lab9_20220270.Dao.DaoArbitros;
import com.example.lab9_20220270.Dao.DaoPartidos;
import com.example.lab9_20220270.Dao.DaoSelecciones;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "PartidoServlet", urlPatterns = {"/PartidoServlet", ""})
public class PartidoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "guardar" : request.getParameter("action");

        switch (action) {
            case "guardar":
                String numeroJornadaStr = request.getParameter("jornada");
                String fecha = request.getParameter("fecha");
                String seleccionLocalStr = request.getParameter("local");
                String seleccionVisitanteStr = request.getParameter("visitante");
                String arbitroStr = request.getParameter("arbitro");

                // Validación de campos completados
                if (numeroJornadaStr == null || numeroJornadaStr.trim().isEmpty() ||
                        fecha == null || fecha.trim().isEmpty() ||
                        seleccionLocalStr == null || seleccionLocalStr.trim().isEmpty() ||
                        seleccionVisitanteStr == null || seleccionVisitanteStr.trim().isEmpty() ||
                        arbitroStr == null || arbitroStr.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/PartidoServlet?action=crear");
                    return;
                }

                int numeroJornada, seleccionLocal, seleccionVisitante, arbitro;

                try {numeroJornada = Integer.parseInt(numeroJornadaStr.trim());
                    seleccionLocal = Integer.parseInt(seleccionLocalStr.trim());
                    seleccionVisitante = Integer.parseInt(seleccionVisitanteStr.trim());
                    arbitro = Integer.parseInt(arbitroStr.trim());
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/PartidoServlet?action=crear");
                    return;
                }

                // Validación de selecciones diferentes
                if (seleccionLocal == seleccionVisitante) {
                    response.sendRedirect(request.getContextPath() + "/PartidoServlet?action=crear");
                    return;
                }

                // Validación de partido duplicado
                DaoPartidos daoPartidos = new DaoPartidos();
                ArrayList<Partido> partidos = daoPartidos.listaDePartidos();
                boolean partidoDuplicado = false;

                for (Partido p : partidos) {
                    if (p.getNumeroJornada() == numeroJornada &&
                            p.getFecha().equals(fecha) &&
                            p.getSeleccionLocal().getIdSeleccion() == seleccionLocal &&
                            p.getSeleccionVisitante().getIdSeleccion() == seleccionVisitante &&
                            p.getArbitro().getIdArbitro() == arbitro) {
                        partidoDuplicado = true;
                        break;
                    }
                }

                if (partidoDuplicado) {
                    response.sendRedirect(request.getContextPath() + "/PartidoServlet?action=crear");
                    return;
                }

                Partido partido = new Partido();
                partido.setNumeroJornada(numeroJornada);
                partido.setFecha(fecha);
                partido.setSeleccionLocal(new DaoSelecciones().buscarSeleccion(seleccionLocal));
                partido.setSeleccionVisitante(new DaoSelecciones().buscarSeleccion(seleccionVisitante));
                partido.setArbitro(new DaoArbitros().buscarArbitro(arbitro));

                daoPartidos.crearPartido(partido);

                response.sendRedirect(request.getContextPath() + "/PartidoServlet");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action") == null ? "lista" : request.getParameter("action");
        RequestDispatcher view;
        DaoPartidos daoPartidos = new DaoPartidos();
        DaoSelecciones daoSelecciones = new DaoSelecciones();
        DaoArbitros daoArbitros = new DaoArbitros();

        switch (action) {
            case "lista":
                ArrayList<Partido> partidos = daoPartidos.listaDePartidos();
                request.setAttribute("listaDePartidos", partidos);
                view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
                break;
            case "crear":
                request.setAttribute("listaSelecciones", daoSelecciones.listarSelecciones());
                request.setAttribute("listaArbitros", daoArbitros.listarArbitros());
                view = request.getRequestDispatcher("partidos/form.jsp");
                view.forward(request, response);
                break;
        }
    }
}

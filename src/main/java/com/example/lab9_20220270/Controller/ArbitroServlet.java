package com.example.lab9_20220270.Controller;

import com.example.lab9_20220270.Bean.Arbitro;
import com.example.lab9_20220270.Dao.DaoArbitros;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "ArbitroServlet", urlPatterns = {"/ArbitroServlet"})
public class ArbitroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "lista" : request.getParameter("action");
        RequestDispatcher view;

        switch (action) {

            case "buscar":
                String busqueda = request.getParameter("buscar").trim();
                String tipo = request.getParameter("tipo").trim();

                DaoArbitros daoArbitros = new DaoArbitros();
                ArrayList<Arbitro> resultados = new ArrayList<>();

                if ("nombre".equalsIgnoreCase(tipo)) {
                    resultados = daoArbitros.busquedaNombre(busqueda);
                } else if ("pais".equalsIgnoreCase(tipo)) {
                    resultados = daoArbitros.busquedaPais(busqueda);
                }

                request.setAttribute("listaDeArbitros", resultados);
                view = request.getRequestDispatcher("/arbitros/list.jsp");
                view.forward(request, response);
                break;

            case "guardar":
                String nombre = request.getParameter("nombre").trim();
                String pais = request.getParameter("pais").trim();

                // Validación de campos completados
                if (nombre.isEmpty() || pais.isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/ArbitroServlet?action=crear");
                    return;
                }

                // Validación de nombre duplicado
                DaoArbitros daoGuardar = new DaoArbitros();
                ArrayList<Arbitro> arbitrosExistentes = daoGuardar.listarArbitros();
                boolean duplicado = false;

                for (Arbitro a : arbitrosExistentes) {
                    if (a.getNombre().equalsIgnoreCase(nombre) && a.getPais().equalsIgnoreCase(pais)) {
                        duplicado = true;
                        break;
                    }
                }

                if (duplicado) {
                    response.sendRedirect(request.getContextPath() + "/ArbitroServlet?action=crear");
                    return;
                }

                Arbitro nuevoArbitro = new Arbitro();
                nuevoArbitro.setNombre(nombre);
                nuevoArbitro.setPais(pais);
                daoGuardar.crearArbitro(nuevoArbitro);

                response.sendRedirect(request.getContextPath() + "/ArbitroServlet");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action") == null ? "lista" : request.getParameter("action");
        RequestDispatcher view;
        ArrayList<String> paises = new ArrayList<>();
        paises.add("Peru");
        paises.add("Chile");
        paises.add("Argentina");
        paises.add("Paraguay");
        paises.add("Uruguay");
        paises.add("Colombia");

        switch (action) {
            case "lista":
                DaoArbitros daoArbitros = new DaoArbitros();
                ArrayList<Arbitro> listaDeArbitros = daoArbitros.listarArbitros();
                request.setAttribute("listaDeArbitros", listaDeArbitros);
                view = request.getRequestDispatcher("/arbitros/list.jsp");
                view.forward(request, response);
                break;
            case "crear":
                request.setAttribute("paises", paises);
                view = request.getRequestDispatcher("/arbitros/form.jsp");
                view.forward(request, response);
                break;
            case "borrar":
                //Comentario: Es necesario tener en cuenta que sólo se podrá borrar un árbitro si este no está conectado a
                //ningún partido. Si fuese árbitro de algún partido entonces fallará la eliminación. Para solucionar esto se
                //podría implementar el metodo borrarArbitro de tal manera que, antes de eliminar el registro del árbitro,
                //elimine todos los registros de partidos relacionados con este registro mediante una llave foránea.
                //No obstante, para la resolución de este laboratorio no se implementó la eliminación de partidos, puesto
                //que no se especificó cómo proceder en dichos casos y consideré que no sería totalmente adecuado eliminar
                //registros de partidos debido a la eliminación de algún árbitro. De todos modos, dentro de la función
                //borrarArbitro en DaoArbitro están incluidas en un comentario las líneas de código que implementarian dicha
                //funcionalidad.
                int idArbitro = Integer.parseInt(request.getParameter("id"));
                DaoArbitros daoBorrar = new DaoArbitros();
                Arbitro arbitroExistente = daoBorrar.buscarArbitro(idArbitro);

                if (arbitroExistente != null) {
                    daoBorrar.borrarArbitro(idArbitro);
                }

                response.sendRedirect(request.getContextPath() + "/ArbitroServlet");
                break;
        }
    }
}

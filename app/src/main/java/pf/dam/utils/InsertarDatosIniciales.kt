package pf.dam.utils


import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.Articulo
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.Prestamo
import pf.dam.socios.Genero
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Locale


class InsertarDatosIniciales {

    fun insertarSociosIniciales(dbSocios: SociosSQLite) {
        val socios = listOf(
            Socio(
                1,
                "Samuel",
                "Correa Pazos",
                1,
                666666666,
                "mail@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("2024-11-22"),
                SimpleDateFormat("yyyy-MM-dd").parse("2024-11-22"),
                Genero.HOMBRE
            ),
            Socio(
                2,
                "Lucía",
                "González",
                2,
                655555555,
                "lucia.gonzalez@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1992-03-15"),
                SimpleDateFormat("yyyy-MM-dd").parse("2023-01-10"),
                Genero.MUJER
            ),
            Socio(
                3,
                "Martín",
                "Díaz",
                3,
                644444444,
                "martin.diaz@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1985-07-29"),
                SimpleDateFormat("yyyy-MM-dd").parse("2022-06-18"),
                Genero.HOMBRE
            ),
            Socio(
                4,
                "Ana",
                "Martínez",
                4,
                633333333,
                "ana.martinez@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1993-02-08"),
                SimpleDateFormat("yyyy-MM-dd").parse("2021-12-05"),
                Genero.MUJER
            ),
            Socio(
                5,
                "Carlos",
                "Fernández",
                5,
                622222222,
                "carlos.fernandez@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1980-04-10"),
                SimpleDateFormat("yyyy-MM-dd").parse("2022-07-14"),
                Genero.HOMBRE
            ),
            Socio(
                6,
                "María",
                "Ruiz",
                6,
                611111111,
                "maria.ruiz@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1995-09-12"),
                SimpleDateFormat("yyyy-MM-dd").parse("2022-09-25"),
                Genero.MUJER
            ),
            Socio(
                7,
                "Juan",
                "Jiménez",
                7,
                699999999,
                "juan.jimenez@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1988-12-23"),
                SimpleDateFormat("yyyy-MM-dd").parse("2022-05-18"),
                Genero.HOMBRE
            ),
            Socio(
                8,
                "Laura",
                "Pérez",
                8,
                688888888,
                "laura.perez@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1997-06-18"),
                SimpleDateFormat("yyyy-MM-dd").parse("2023-02-13"),
                Genero.MUJER
            ),
            Socio(
                9,
                "Pedro",
                "Romero",
                9,
                677777777,
                "pedro.romero@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1983-10-03"),
                SimpleDateFormat("yyyy-MM-dd").parse("2022-04-15"),
                Genero.HOMBRE
            ),
            Socio(
                10,
                "Elena",
                "Moreno",
                10,
                666666667,
                "elena.moreno@ejm.com",
                SimpleDateFormat("yyyy-MM-dd").parse("1987-01-26"),
                SimpleDateFormat("yyyy-MM-dd").parse("2022-08-30"),
                Genero.MUJER
            ))
                    socios.forEach { socio ->
                dbSocios.insertarSocio(socio)

                    }
    }

    fun insertarArticulosIniciales(dbArticulos: ArticulosSQLite){
        val articulos = listOf(
            Articulo(null, "Bicicleta", "MTB", "Bici001", "D01", EstadoArticulo.DISPONIBLE, ""),
            Articulo(         null, "Bicicleta", "Carretera", "Bici002", "D02",
                EstadoArticulo.DISPONIBLE, ""     ),


        Articulo(
            null,
            "Bicicleta",
            "Eléctrica",
            "Bici003",
            "D03",
            EstadoArticulo.DISPONIBLE,
            ""),


        Articulo(
            null,
            "Kayak",
            "Rígido",
            "Kayak001",
            "D01",
            EstadoArticulo.DISPONIBLE,
            "" ),


        Articulo(
            null, "Kayak", "Hinchable", "Kayak002", "D02",
            EstadoArticulo.DISPONIBLE, ""),

        Articulo(
            null,
            "Kayak",
            "Eléctrica",
            "Kayak003",
            "D03",
            EstadoArticulo.DISPONIBLE,
            ""),


        Articulo(
            null, "PadellSurf", "Hinchable", "Tabla001", "D02",
            EstadoArticulo.DISPONIBLE, "" ),


        Articulo(
            null,
            "Patinete",
            "Eléctrica",
            "Patín001",
            "D03",
            EstadoArticulo.NO_DISPONIBLE,
            ""
        ))
        articulos.forEach { articulo ->
            dbArticulos.insertarArticulo(articulo)

        }
    }

    fun insertarPrestamosIniciales(dbPrestamos: PrestamosSQLite){
        val prestamos = listOf(
            Prestamo(1, 1, 1, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("01-09-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("10-09-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(2, 2, 1, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("02-09-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("11-09-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(3, 3, 1, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("03-09-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("10-10-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(4, 4, 1, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("16-09-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("10-10-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(5, 5, 6, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("30-09-2024"), null,
                null.toString(),
                EstadoPrestamo.ACTIVO
            ),
            Prestamo(6, 1, 5, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("01-10-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("10-11-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(7, 2, 4, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("02-10-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("11-10-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(8, 3, 3, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("03-11-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("10-12-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            ),
            Prestamo(9, 4, 2, SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("16-11-2024"), SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).parse("10-12-2024"),
                null.toString(),
                EstadoPrestamo.CERRADO
            )
        )
        prestamos.forEach { prestamo ->
            dbPrestamos.insertarPrestamo(prestamo)

        }
    }
}
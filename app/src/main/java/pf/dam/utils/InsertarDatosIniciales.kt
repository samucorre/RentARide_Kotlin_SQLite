package pf.dam.utils


import android.annotation.SuppressLint
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

        @SuppressLint("SuspiciousIndentation")
    fun insertarSociosIniciales(dbSocios: SociosSQLite) {
            val socios = listOf(
                Socio(
                    1, "Samuel", "Correa Pazos", 100001, 666666666, "samuel@ejm.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("20-11-1985"),
                    SimpleDateFormat("dd-MM-yyyy").parse("20-11-2022"), Genero.HOMBRE
                ),
                Socio(
                    2, "Laura", "García López", 100002, 612345678, "laura.garcia@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("15-06-1975"),
                    SimpleDateFormat("dd-MM-yyyy").parse("01-01-2022"), Genero.MUJER
                ),
                Socio(
                    3, "Javier", "Martínez Ruiz", 100003, 612345679, "javier.martinez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("30-04-1980"),
                    SimpleDateFormat("dd-MM-yyyy").parse("12-05-2021"), Genero.HOMBRE
                ),
                Socio(
                    4, "Ana", "Pérez Fernández", 100004, 612345680, "ana.perez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("21-08-1988"),
                    SimpleDateFormat("dd-MM-yyyy").parse("25-12-2023"), Genero.MUJER
                ),
                Socio(
                    5, "Carlos", "Sánchez Gómez", 100005, 612345681, "carlos.sanchez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("03-11-1970"),
                    SimpleDateFormat("dd-MM-yyyy").parse("30-09-2022"), Genero.HOMBRE
                ),
                Socio(
                    6, "Clara", "Torres Méndez", 100006, 612345682, "clara.torres@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("05-02-1968"),
                    SimpleDateFormat("dd-MM-yyyy").parse("15-03-2024"), Genero.MUJER
                ),
                Socio(
                    7, "Fernando", "Hernández Ruiz", 100007, 612345683, "fernando.hernandez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("14-07-1982"),
                    SimpleDateFormat("dd-MM-yyyy").parse("18-10-2021"), Genero.HOMBRE
                ),
                Socio(
                    8, "Marta", "Ramírez López", 100008, 612345684, "marta.ramirez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("09-05-1972"),
                    SimpleDateFormat("dd-MM-yyyy").parse("22-07-2022"), Genero.MUJER
                ),
                Socio(
                    9, "Luis", "Jiménez Pérez", 100009, 612345685, "luis.jimenez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("11-12-1966"),
                    SimpleDateFormat("dd-MM-yyyy").parse("30-08-2023"), Genero.HOMBRE
                ),
                Socio(
                    10, "Isabel", "Castillo Martínez", 100010, 612345686, "isabel.castillo@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("17-03-1985"),
                    SimpleDateFormat("dd-MM-yyyy").parse("27-11-2022"), Genero.MUJER
                ),
                Socio(
                    11, "Ricardo", "Morales García", 100011, 612345687, "ricardo.morales@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("23-09-1978"),
                    SimpleDateFormat("dd-MM-yyyy").parse("04-01-2023"), Genero.HOMBRE
                ),
                Socio(
                    12, "Patricia", "Cruz Sánchez", 100012, 612345688, "patricia.cruz@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("01-01-1983"),
                    SimpleDateFormat("dd-MM-yyyy").parse("11-11-2022"), Genero.MUJER
                ),
                Socio(
                    13, "Eduardo", "Reyes Torres", 100013, 612345689, "eduardo.reyes@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("28-10-1975"),
                    SimpleDateFormat("dd-MM-yyyy").parse("19-02-2024"), Genero.HOMBRE
                ),
                Socio(
                    14, "Sofía", "Mendoza Flores", 100014, 612345690, "sofia.mendoza@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("12-04-1989"),
                    SimpleDateFormat("dd-MM-yyyy").parse("30-06-2024"), Genero.MUJER
                ),
                Socio(
                    15, "Alberto", "Vega Ríos", 100015, 612345691, "alberto.vega@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("16-12-1969"),
                    SimpleDateFormat("dd-MM-yyyy").parse("03-03-2021"), Genero.HOMBRE
                ),
                Socio(
                    16, "Verónica", "Salinas Romero", 100016, 612345692, "veronica.salinas@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("19-07-1981"),
                    SimpleDateFormat("dd-MM-yyyy").parse("14-02-2023"), Genero.MUJER
                ),
                Socio(
                    17, "Jorge", "Pineda Quiroz", 100017, 612345693, "jorge.pineda@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("02-09-1974"),
                    SimpleDateFormat("dd-MM-yyyy").parse("27-12-2022"), Genero.HOMBRE
                ),
                Socio(
                    18, "Natalia", "Gómez Alvarado", 100018, 612345694, "natalia.gomez@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("30-08-1986"),
                    SimpleDateFormat("dd-MM-yyyy").parse("10-10-2024"), Genero.MUJER
                ),
                Socio(
                    19, "Diego", "Salazar Cortés", 100019, 612345695, "diego.salazar@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("25-05-1982"),
                    SimpleDateFormat("dd-MM-yyyy").parse("29-01-2024"), Genero.HOMBRE
                ),
                Socio(
                    20, "Elena", "Figueroa León", 100020, 612345696, "elena.figueroa@mail.com",
                    SimpleDateFormat("dd-MM-yyyy").parse("18-11-1967"),
                    SimpleDateFormat("dd-MM-yyyy").parse("12-09-2023"), Genero.MUJER
                )
            )
            socios.forEach { socio ->
                dbSocios.addSocio(socio)
            }
        }

    fun insertarArticulosIniciales(dbArticulos: ArticulosSQLite) {
        val articulos = listOf(
            Articulo(null, "Bicicleta", "MTB", "Bici001", "D01", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "Bicicleta", "Carretera", "Bici002", "D02", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "Bicicleta", "Eléctrica", "Bici003", "D03", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "Kayak", "Rígido", "Kayak001", "D01", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "Kayak", "Hinchable", "Kayak002", "D02", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "Kayak", "Eléctrica", "Kayak003", "D03", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "PadellSurf", "Hinchable", "Tabla001", "D02", EstadoArticulo.DISPONIBLE, ""),
            Articulo(null, "Patinete", "Eléctrica", "Patín001", "D03", EstadoArticulo.NO_DISPONIBLE, "")
        )
        articulos.forEach { articulo ->
            dbArticulos.addArticulo(articulo)
        }
    }

    fun insertarPrestamosIniciales(dbPrestamos: PrestamosSQLite) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val prestamos = listOf(
            Prestamo(1, 1, 1, dateFormat.parse("01-09-2024"), dateFormat.parse("10-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(2, 2, 1, dateFormat.parse("02-09-2024"), dateFormat.parse("11-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(3, 3, 1, dateFormat.parse("03-09-2024"), dateFormat.parse("10-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(4, 4, 1, dateFormat.parse("16-09-2024"), dateFormat.parse("10-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(5, 5, 6, dateFormat.parse("30-09-2024"), dateFormat.parse("05-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(6, 1, 5, dateFormat.parse("01-10-2024"), dateFormat.parse("10-11-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(7, 2, 4, dateFormat.parse("02-10-2024"), dateFormat.parse("11-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(8, 3, 3, dateFormat.parse("03-11-2024"), dateFormat.parse("10-12-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(9, 4, 2, dateFormat.parse("16-11-2024"), dateFormat.parse("10-12-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(10, 5, 1, dateFormat.parse("05-09-2024"), dateFormat.parse("15-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(11, 6, 2, dateFormat.parse("06-09-2024"), dateFormat.parse("20-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(12, 1, 3, dateFormat.parse("07-09-2024"), dateFormat.parse("17-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(13, 3, 4, dateFormat.parse("08-09-2024"), dateFormat.parse("18-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(14, 3, 5, dateFormat.parse("09-09-2024"), dateFormat.parse("19-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(15, 1, 6, dateFormat.parse("10-09-2024"), dateFormat.parse("20-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(16, 1, 1, dateFormat.parse("11-09-2024"), dateFormat.parse("21-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(17, 2, 2, dateFormat.parse("12-09-2024"), dateFormat.parse("22-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(18, 3, 3, dateFormat.parse("13-09-2024"), dateFormat.parse("23-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(19, 4, 4, dateFormat.parse("14-09-2024"), dateFormat.parse("24-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(20, 5, 5, dateFormat.parse("15-09-2024"), dateFormat.parse("25-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(21, 6, 6, dateFormat.parse("20-09-2024"), dateFormat.parse("30-09-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(22, 5, 7, dateFormat.parse("21-09-2024"), dateFormat.parse("01-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(23, 6, 8, dateFormat.parse("22-09-2024"), dateFormat.parse("02-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(24, 4, 9, dateFormat.parse("23-09-2024"), dateFormat.parse("03-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(25, 2, 10, dateFormat.parse("24-09-2024"), dateFormat.parse("04-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(26, 1, 11, dateFormat.parse("25-09-2024"), dateFormat.parse("05-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(27, 3, 1, dateFormat.parse("26-09-2024"), dateFormat.parse("06-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(28, 3, 2, dateFormat.parse("27-09-2024"), dateFormat.parse("07-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(29, 4, 3, dateFormat.parse("28-09-2024"), dateFormat.parse("08-10-2024"), null.toString(), EstadoPrestamo.CERRADO),
            Prestamo(30, 5, 4, dateFormat.parse("29-09-2024"), dateFormat.parse("09-10-2024"), null.toString(), EstadoPrestamo.CERRADO)
        )

        prestamos.forEach { prestamo ->
            dbPrestamos.addrPrestamo(prestamo)
        }
    }
}
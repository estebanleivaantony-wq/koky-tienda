package com.koky.web.service;

import com.koky.web.domain.Producto;
import com.koky.web.domain.Rol;
import com.koky.web.domain.Usuario;
import com.koky.web.repo.ProductoRepo;
import com.koky.web.repo.RolRepo;
import com.koky.web.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

        @Autowired
        private RolRepo rolRepo;
        @Autowired
        private UsuarioRepo usuarioRepo;
        @Autowired
        private ProductoRepo productoRepo;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                initializeRoles();
                initializeUsers();
                initializeProducts();
        }

        private void initializeRoles() {
                if (rolRepo.findByName("ADMIN").isEmpty())
                        rolRepo.save(new Rol("ADMIN"));
                if (rolRepo.findByName("USER").isEmpty())
                        rolRepo.save(new Rol("USER"));
        }

        private void initializeUsers() {
                if (usuarioRepo.findByEmail("admin@koky.com").isEmpty()) {
                        Rol adminRole = rolRepo.findByName("ADMIN").orElseThrow();
                        Usuario adminUser = new Usuario("Admin", "Koky", "admin@koky.com",
                                        passwordEncoder.encode("admin123"), 1000);
                        adminUser.setRoles(Collections.singletonList(adminRole));
                        usuarioRepo.save(adminUser);
                }
                if (usuarioRepo.findByEmail("cliente@koky.com").isEmpty()) {
                        Rol userRole = rolRepo.findByName("USER").orElseThrow();
                        Usuario regularUser = new Usuario("Juan", "Perez", "cliente@koky.com",
                                        passwordEncoder.encode("cliente123"), 500);
                        regularUser.setRoles(Collections.singletonList(userRole));
                        usuarioRepo.save(regularUser);
                }
        }

        private void initializeProducts() {
                // IMPORTANTE: Si la tabla ya tiene datos, no hacemos nada.
                // Por eso en el PASO 1 te pedí borrar la tabla (TRUNCATE).
                if (productoRepo.count() > 0) {
                        System.out.println(">>> La base de datos ya contiene productos. Omitiendo carga.");
                        return;
                }

                List<Producto> productos = new ArrayList<>();

                // --- NOVEDADES (Index) ---
                productos.add(new Producto("Marcador Filgo Tinta al Agua", "Marcador versátil para pizarra.",
                                new BigDecimal("39.90"), new BigDecimal("52.90"),
                                "img/Marcador Filgo Tinta al Agua.webp", "Oficina"));
                productos.add(new Producto("Vaso Térmico Amayra 120ml", "Mantiene temperatura ideal.",
                                new BigDecimal("34.90"), null, "img/Vaso Térmico Amayra 120ml.webp", "Regalo"));
                productos.add(new Producto("Marcador Acrílico Doble Punta", "Colores intensos.",
                                new BigDecimal("65.90"), new BigDecimal("82.50"),
                                "img/Marcador Acrílico Doble Punta.webp", "Oficina"));
                productos.add(new Producto("Mochila JanSport", "Calidad y estilo clásico.", new BigDecimal("75.50"),
                                null, "img/Mochila JanSport.webp", "Escolar"));

                // --- TENDENCIAS (Lilo & Stitch) ---
                productos.add(new Producto("Mochila de Stitch", "Mochila temática azul.", new BigDecimal("129.90"),
                                null, "img/mochilaStich.jpg", "Stitch"));
                productos.add(new Producto("Cuaderno Lilo & Stitch", "A4 Cuadriculado.", new BigDecimal("24.50"), null,
                                "img/cuadernoLilo&Stich.jpg", "Stitch"));
                productos.add(new Producto("Taza Mágica de Stitch", "Cambia con el calor.", new BigDecimal("39.90"),
                                null, "img/tazaMagicaStitch.jpg", "Stitch"));
                productos.add(new Producto("Peluche de Stitch 30cm", "Suave y original.", new BigDecimal("89.90"), null,
                                "img/PelucheStitch.jpg", "Stitch"));
                productos.add(new Producto("Set de Lapiceros Stitch", "Pack x5 tinta gel.", new BigDecimal("29.90"),
                                null, "img/SetLapicerosStitch.jpg", "Stitch"));
                productos.add(new Producto("Funko Pop! de Lilo", "Figura coleccionable.", new BigDecimal("65.00"), null,
                                "img/FunkoPopLilo.jpg", "Stitch"));
                productos.add(new Producto("Gorra Bordada de Stitch", "Ajustable y fresca.", new BigDecimal("45.00"),
                                null, "img/gorraBordadaStitch.webp", "Stitch"));
                productos.add(new Producto("Pantuflas de Stitch", "Comodidad en casa.", new BigDecimal("55.90"), null,
                                "img/pantuflasStitch.jpg", "Stitch"));
                productos.add(new Producto("Cartuchera de Scrump", "Diseño único.", new BigDecimal("32.00"), null,
                                "img/CartucheraStitch.webp", "Stitch"));
                productos.add(new Producto("Llavero Metálico Angel", "Resistente y bonito.", new BigDecimal("19.90"),
                                null, "img/llaveroAngelStitch.jpg", "Stitch"));
                productos.add(new Producto("Pijama de Stitch", "Enterizo Kigurumi.", new BigDecimal("99.90"), null,
                                "img/pijamaStitch.jpeg", "Stitch"));
                productos.add(new Producto("Botella Tomatodo", "Aluminio 600ml.", new BigDecimal("38.50"), null,
                                "img/TomatodoStitch.webp", "Stitch"));
                productos.add(new Producto("Stickers Holográficos", "Resistentes al agua.", new BigDecimal("15.00"),
                                null, "img/StickersStitch.jpg", "Stitch"));
                productos.add(new Producto("Póster Enmarcado", "Decoración de pared.", new BigDecimal("50.00"), null,
                                "img/PosterStitch.webp", "Stitch"));
                productos.add(new Producto("Medias de Stitch", "Algodón suave.", new BigDecimal("22.90"), null,
                                "img/mediasStitch.jpg", "Stitch"));
                productos.add(new Producto("Agenda 2025 Stitch", "Planificador anual.", new BigDecimal("49.90"), null,
                                "img/AgendaStitch.webp", "Stitch"));
                productos.add(new Producto("Lámpara LED 3D Stitch", "Luz de noche.", new BigDecimal("75.00"), null,
                                "img/LamparaLedStitch.webp", "Stitch"));
                productos.add(new Producto("Mousepad Gamer Stitch", "Superficie amplia.", new BigDecimal("42.00"), null,
                                "img/MousePadStitch.jpg", "Stitch"));
                productos.add(new Producto("Pin Esmaltado Stitch", "Para ropa o mochila.", new BigDecimal("18.00"),
                                null, "img/PinStitch.webp", "Stitch"));
                productos.add(new Producto("Lonchera Térmica", "Mantiene el calor.", new BigDecimal("59.90"), null,
                                "img/LoncheraStitch.jpg", "Stitch"));
                productos.add(new Producto("Cargador Portatil", "Powerbank 5000mAh.", new BigDecimal("85.00"), null,
                                "img/CargadorPortatilStitch.jpeg", "Stitch"));
                productos.add(new Producto("Libreta de Notas", "Bolsillo práctica.", new BigDecimal("28.00"), null,
                                "img/LibretaStitch.jpg", "Stitch"));
                productos.add(new Producto("Vaso con Cañita", "Reutilizable.", new BigDecimal("35.00"), null,
                                "img/vasoStitch.webp", "Stitch"));
                productos.add(new Producto("Set de Maquillaje", "Estuche completo.", new BigDecimal("79.90"), null,
                                "img/SetMaquillajeStitch.webp", "Stitch"));
                productos.add(new Producto("Audífonos Bluetooth", "Sonido estéreo.", new BigDecimal("119.90"), null,
                                "img/AudifonosStitch.webp", "Stitch"));
                productos.add(new Producto("Paraguas Plegable", "Compacto y resistente.", new BigDecimal("48.00"), null,
                                "img/paraguaStitch.webp", "Stitch"));
                productos.add(new Producto("Juego de Cama", "1.5 Plazas.", new BigDecimal("189.90"), null,
                                "img/juegoDeCamaStitch.jpg", "Stitch"));
                productos.add(new Producto("Funda para Laptop", "Acolchada 15p.", new BigDecimal("62.50"), null,
                                "img/fundLaptopStitch.png", "Stitch"));
                productos.add(new Producto("Reloj de Pared", "Analógico silencioso.", new BigDecimal("55.00"), null,
                                "img/relojDeParedStitch.webp", "Stitch"));
                productos.add(new Producto("Set de Resaltadores", "Colores pastel.", new BigDecimal("34.90"), null,
                                "img/SetResaltadoresStitch.jpeg", "Stitch"));

                // --- ESCOLAR ---
                productos.add(new Producto("Mochila Porta 15.6''", "Ideal para laptop.", new BigDecimal("99.90"), null,
                                "img/MochilaPorta.webp", "Escolar"));
                productos.add(new Producto("Caja de Colores x24", "Faber-Castell.", new BigDecimal("18.50"), null,
                                "img/cajaColores.webp", "Escolar"));
                productos.add(new Producto("Cuaderno Cuadriculado", "Standford Loro.", new BigDecimal("7.90"), null,
                                "img/CuadernoCuadriculado.jpg", "Escolar"));
                productos.add(new Producto("Lonchera Térmica Infantil", "Diseños variados.", new BigDecimal("45.00"),
                                null, "img/LoncheraTermica.webp", "Escolar"));
                productos.add(new Producto("Set de Geometría Acrílico", "Reglas completas.", new BigDecimal("12.00"),
                                null, "img/setGeometria.webp", "Escolar"));
                productos.add(new Producto("Tijera Escolar Punta Roma", "Segura para niños.", new BigDecimal("4.50"),
                                null, "img/tijeraPuntaRoma.webp", "Escolar"));
                productos.add(new Producto("Goma en Barra UHU 40g", "Pegado fuerte.", new BigDecimal("6.00"), null,
                                "img/gomaBarra.webp", "Escolar"));
                productos.add(new Producto("Plumones Delgados x12", "Artesco lavables.", new BigDecimal("15.90"), null,
                                "img/plumonesDelgados.webp", "Escolar"));
                productos.add(new Producto("Cartuchera de Lona 2 Cierres", "Gran capacidad.", new BigDecimal("25.00"),
                                null, "img/cartuchera2cierres.webp", "Escolar"));
                productos.add(new Producto("Block de Dibujo A4 Skecth", "Hojas bond.", new BigDecimal("9.50"), null,
                                "img/blockDIBUJOa4.jpg", "Escolar"));
                productos.add(new Producto("Forro de Cuaderno Adhesivo", "Transparente.", new BigDecimal("5.00"), null,
                                "img/forroAdesivo.png", "Escolar"));
                productos.add(new Producto("Temperas Artesco x12", "No tóxicas.", new BigDecimal("14.90"), null,
                                "img/temperasArtesco.webp", "Escolar"));
                productos.add(new Producto("Lápiz Bicolor Standford", "Rojo y azul.", new BigDecimal("2.50"), null,
                                "img/lapizBicolor.webp", "Escolar"));
                productos.add(new Producto("Borrador Blanco Faber", "No mancha.", new BigDecimal("1.50"), null,
                                "img/borradorBLanco.webp", "Escolar"));
                productos.add(new Producto("Tajador con Depósito", "Limpio y práctico.", new BigDecimal("3.00"), null,
                                "img/tajadorDeposito.webp", "Escolar"));
                productos.add(new Producto("Corrector Líquido Faber", "Secado rápido.", new BigDecimal("4.00"), null,
                                "img/correctorLiquido.webp", "Escolar"));
                productos.add(new Producto("Papel Lustre x Pliego", "Colores vivos.", new BigDecimal("1.00"), null,
                                "img/papelLustre.webp", "Escolar"));
                productos.add(new Producto("Plastilina Artesco x12", "Suave modelado.", new BigDecimal("8.50"), null,
                                "img/plastilinaArtesco.webp", "Escolar"));
                productos.add(new Producto("Resaltadores Pastel Faber", "Set x4.", new BigDecimal("22.00"), null,
                                "img/resaltadoresPastel.webp", "Escolar"));
                productos.add(new Producto("Folder Manila A4", "Paquete escolar.", new BigDecimal("0.80"), null,
                                "img/folderManila.jpg", "Escolar"));
                productos.add(new Producto("Calculadora Científica Casio", "Funciones avanzadas.",
                                new BigDecimal("55.00"), null, "img/calculadoraCientifica.jpg", "Escolar"));
                productos.add(new Producto("Cinta Adhesiva Gruesa", "Embalaje.", new BigDecimal("3.50"), null,
                                "img/cintaAdhesivagruesa.webp", "Escolar"));
                productos.add(new Producto("Papel Bond A4 500 Hojas", "75gr Ultra.", new BigDecimal("19.90"), null,
                                "img/papelBond500hojas.webp", "Escolar"));
                productos.add(new Producto("Juego de Pinceles", "Puntas variadas.", new BigDecimal("11.00"), null,
                                "img/juegoPincelesx5.webp", "Escolar"));
                productos.add(new Producto("Block de Cartulina", "Colores creativos.", new BigDecimal("12.50"), null,
                                "img/BlockDeCartulina.webp", "Escolar"));
                productos.add(new Producto("Silicona Líquida 100ml", "Multiuso.", new BigDecimal("5.50"), null,
                                "img/siliconaLiquida.webp", "Escolar"));
                productos.add(new Producto("Crayolas Jumbo x12", "Para preescolar.", new BigDecimal("9.90"), null,
                                "img/crayolas jumbo x12.webp", "Escolar"));
                productos.add(new Producto("Compás de Precisión", "Metálico.", new BigDecimal("8.00"), null,
                                "img/compas de precision.webp", "Escolar"));
                productos.add(new Producto("Diccionario Escolar", "Español.", new BigDecimal("25.00"), null,
                                "img/diccionarioEscoalr.webp", "Escolar"));
                productos.add(new Producto("Mochila con Ruedas", "Ergonómica.", new BigDecimal("150.00"), null,
                                "img/mochilaRuedas.webp", "Escolar"));

                // --- OFICINA ---
                productos.add(new Producto("Silla Ergonómica", "Soporte lumbar.", new BigDecimal("349.90"), null,
                                "img/sillaErgonomicaOFICINA.webp", "Oficina"));
                productos.add(new Producto("Resma Papel Bond A4", "80gr Premium.", new BigDecimal("22.50"), null,
                                "img/resmaDePapelOFICINA.webp", "Oficina"));
                productos.add(new Producto("Marcadores de Pizarra", "Set x4.", new BigDecimal("15.00"), null,
                                "img/marcadoresPizarraOFICINA.webp", "Oficina"));
                productos.add(new Producto("Engrapadora de Metal", "Trabajo pesado.", new BigDecimal("28.00"), null,
                                "img/engrapadoraMetalOFICINA.webp", "Oficina"));
                productos.add(new Producto("Archivador de Palanca", "Lomo ancho.", new BigDecimal("12.50"), null,
                                "img/ArchivadorPalancaOFICINA.webp", "Oficina"));
                productos.add(new Producto("Perforador 2 Huecos", "Metálico.", new BigDecimal("18.90"), null,
                                "img/perforador2huecosOFICINA.webp", "Oficina"));
                productos.add(new Producto("Tinta para Impresora", "Negro 100ml.", new BigDecimal("45.00"), null,
                                "img/tintaImpresoraOFICINA.webp", "Oficina"));
                productos.add(new Producto("Caja de Clips x100", "Galvanizados.", new BigDecimal("3.50"), null,
                                "img/cajaClips100oFICINA.webp", "Oficina"));
                productos.add(new Producto("Agenda Ejecutiva 2025", "Cuero sintético.", new BigDecimal("55.00"), null,
                                "img/agendaEjecutiva2025OFICINA.webp", "Oficina"));
                productos.add(new Producto("Toner Compatible HP", "Alto rendimiento.", new BigDecimal("89.90"), null,
                                "img/tonerHP OFICINA.jpg", "Oficina"));
                productos.add(new Producto("Notas Adhesivas", "Tipo Post-it.", new BigDecimal("9.90"), null,
                                "img/notasAdhesivasPosItOFICIAN.webp", "Oficina"));
                productos.add(new Producto("Dispensador de Cinta", "Base pesada.", new BigDecimal("19.00"), null,
                                "img/dispensador DeCintaOFICINA.jpg", "Oficina"));
                productos.add(new Producto("Pizarra de Corcho", "60x40cm.", new BigDecimal("35.00"), null,
                                "img/pizarraCorchoOFICINA.webp", "Oficina"));
                productos.add(new Producto("Lapicero Tinta Líquida", "Pilot V5.", new BigDecimal("6.50"), null,
                                "img/lapiceroTintaLiquidaOFICINA.webp", "Oficina"));
                productos.add(new Producto("Mousepad Ergonómico", "Con gel.", new BigDecimal("25.00"), null,
                                "img/mousepadErgonomicoOFICINA.webp", "Oficina"));
                productos.add(new Producto("Organizador Escritorio", "Malla metálica.", new BigDecimal("38.00"), null,
                                "img/organizadorDeEscritorioOFICINA.jpg", "Oficina"));
                productos.add(new Producto("Trituradora de Papel", "Personal.", new BigDecimal("120.00"), null,
                                "img/trituradoraDePapelOFICINA.jpg", "Oficina"));
                productos.add(new Producto("Lámpara de Escritorio", "LED flexible.", new BigDecimal("65.00"), null,
                                "img/lampraLedOFICINA.jpg", "Oficina"));
                productos.add(new Producto("Sello Automático", "Personalizado.", new BigDecimal("40.00"), null,
                                "img/selloPersonalizadoOFICINA.webp", "Oficina"));
                productos.add(new Producto("Fasteners Plásticos", "Caja x100.", new BigDecimal("8.00"), null,
                                "img/FastenersPlásticos x100OFICINA.webp", "Oficina"));
                productos.add(new Producto("Calculadora Escritorio", "Grande 12 dígitos.", new BigDecimal("32.00"),
                                null, "img/calculadoraDeEscritorioOFICINA.webp", "Oficina"));
                productos.add(new Producto("Porta Lapiceros Malla", "Negro mate.", new BigDecimal("10.00"), null,
                                "img/portaLapicerosMallaOFICINA.webp", "Oficina"));
                productos.add(new Producto("Sobres Manila A4", "Paquete x25.", new BigDecimal("12.00"), null,
                                "img/sobresManillaOFICINA.webp", "Oficina"));
                productos.add(new Producto("Tijera de Oficina 8\"", "Acero inox.", new BigDecimal("9.50"), null,
                                "img/tijeraOficina8OFICINA.jpg", "Oficina"));
                productos.add(new Producto("Guillotina de Papel", "Base metal A4.", new BigDecimal("75.00"), null,
                                "img/guillotinaPapelA4OFICINA.jpg", "Oficina"));
                productos.add(new Producto("Grapas 26/6", "Caja x5000.", new BigDecimal("6.00"), null,
                                "img/grapasOFICINA.webp", "Oficina"));
                productos.add(new Producto("Mouse Inalámbrico", "Logitech.", new BigDecimal("69.90"), null,
                                "img/mouseLogitechOFICINA.jpg", "Oficina"));
                productos.add(new Producto("Pizarra Acrílica", "120x80cm.", new BigDecimal("110.00"), null,
                                "img/pizarraAcrilicaOFICINA.webp", "Oficina"));
                productos.add(new Producto("Extensión Eléctrica", "6 tomas.", new BigDecimal("29.90"), null,
                                "img/extensionOFICINA.webp", "Oficina"));
                productos.add(new Producto("Saca Grapas", "Tipo pinza.", new BigDecimal("4.50"), null,
                                "img/sacaGrapasOFICINA.webp", "Oficina"));

                // --- REGALERÍA ---
                productos.add(new Producto("Taza Personalizada", "Con tu foto.", new BigDecimal("35.00"), null,
                                "img/tazaPersonalizada.webp", "Regalo"));
                productos.add(new Producto("Funko Pop! Grogu", "The Mandalorian.", new BigDecimal("75.00"), null,
                                "img/funkoPopGrogu.png", "Regalo"));
                productos.add(new Producto("Puzzle 1000 Piezas", "Paisajes.", new BigDecimal("65.00"), null,
                                "img/puzzle100piezas.webp", "Regalo"));
                productos.add(new Producto("Vela Aromática", "Vainilla.", new BigDecimal("29.90"), null,
                                "img/vela aromatica.webp", "Regalo"));
                productos.add(new Producto("Marco de Fotos", "Madera.", new BigDecimal("25.00"), null,
                                "img/marcoFotos Madera.webp", "Regalo"));
                productos.add(new Producto("Caja de Regalo", "Premium.", new BigDecimal("15.00"), null,
                                "img/cajaRegaloPremium.webp", "Regalo"));
                productos.add(new Producto("Juego de Mesa Catan", "Original.", new BigDecimal("180.00"), null,
                                "img/juego de mesa CATAN.webp", "Regalo"));
                productos.add(new Producto("Peluche de Aguacate", "Kawaii.", new BigDecimal("45.00"), null,
                                "img/PELUCHE AGUACATE.webp", "Regalo"));
                productos.add(new Producto("Llavero Funko Pop!", "Pocket.", new BigDecimal("28.00"), null,
                                "img/llavero funko pockert.webp", "Regalo"));
                productos.add(new Producto("Tarjeta de Regalo", "KOKY S/50.", new BigDecimal("50.00"), null,
                                "img/tarjeta de regalo.webp", "Regalo"));
                productos.add(new Producto("Planta Suculenta", "Decorativa artificial.", new BigDecimal("22.00"), null,
                                "img/suculenta decorativa.webp", "Regalo"));
                productos.add(new Producto("Bombones Chocolate", "Caja regalo.", new BigDecimal("40.00"), null,
                                "img/caja de bombones.webp", "Regalo"));
                productos.add(new Producto("Álbum Scrapbook", "Para fotos.", new BigDecimal("55.00"), null,
                                "img/Álbum de Fotos Scrapbook.webp", "Regalo"));
                productos.add(new Producto("Manta Personalizada", "Polar suave.", new BigDecimal("89.90"), null,
                                "img/manta personalizada.jpg", "Regalo"));
                productos.add(new Producto("Set de Té", "Tea for one.", new BigDecimal("68.00"), null,
                                "img/set de té para uno.webp", "Regalo"));
                productos.add(new Producto("Cubo de Rubik", "Profesional.", new BigDecimal("32.00"), null,
                                "img/cubo rubick.webp", "Regalo"));
                productos.add(new Producto("Posavasos Cerámica", "Set x4.", new BigDecimal("24.00"), null,
                                "img/set de  posavasos.webp", "Regalo"));
                productos.add(new Producto("Globo Terráqueo", "Decorativo.", new BigDecimal("95.00"), null,
                                "img/globo terraqueo.webp", "Regalo"));
                productos.add(new Producto("Diario de Cuero", "Con lazo.", new BigDecimal("48.00"), null,
                                "img/diario CUERO.webp", "Regalo"));
                productos.add(new Producto("Kit Cerveza", "Artesanal.", new BigDecimal("150.00"), null,
                                "img/kit de cerveza.jpg", "Regalo"));
                productos.add(new Producto("Libro Mandalas", "Para colorear.", new BigDecimal("20.00"), null,
                                "img/libro mandalas adultos.webp", "Regalo"));
                productos.add(new Producto("Caja Musical", "Madera.", new BigDecimal("70.00"), null,
                                "img/caja musical.webp", "Regalo"));
                productos.add(new Producto("Portalapicero", "Diseño.", new BigDecimal("30.00"), null,
                                "img/portaLapiceroDISEÑO.webp", "Regalo"));
                productos.add(new Producto("Copas de Vino", "Set x2.", new BigDecimal("90.00"), null,
                                "img/SET COPAS DE VINO.jpg", "Regalo"));
                productos.add(new Producto("Termo Inteligente", "Pantalla LED.", new BigDecimal("58.00"), null,
                                "img/TERMO INTELIGENTE LED.webp", "Regalo"));
                productos.add(new Producto("Pluma Estilográfica", "Lujo.", new BigDecimal("120.00"), null,
                                "img/PLUMA ESTILOGRAFICA.webp", "Regalo"));
                productos.add(new Producto("Difusor Aromas", "Ultrasónico.", new BigDecimal("85.00"), null,
                                "img/DIFUSOR DE AROMAS.webp", "Regalo"));
                productos.add(new Producto("Kit Barman", "Acero inox.", new BigDecimal("110.00"), null,
                                "img/KIT DE BARMAN.webp", "Regalo"));
                productos.add(new Producto("Papel Regalo", "Set con cintas.", new BigDecimal("18.00"), null,
                                "img/SET DE PAPEL Y CINTAS.webp", "Regalo"));
                productos.add(new Producto("Ajedrez Lujo", "Madera.", new BigDecimal("160.00"), null,
                                "img/AJEDREZ DE LUJO.webp", "Regalo"));

                // --- MARCAS Y TEMÁTICAS ---
                productos.add(new Producto("Set Harry Potter", "Completo.", new BigDecimal("159.90"), null,
                                "img/set completo harry potter.jpg", "Marca"));
                productos.add(new Producto("Mochila Avengers", "Marvel.", new BigDecimal("119.90"), null,
                                "img/mochila marve avengers.webp", "Marca"));
                productos.add(new Producto("Agenda Hello Kitty", "2025.", new BigDecimal("49.90"), null,
                                "img/agenda hola demonio.webp", "Marca"));
                productos.add(new Producto("Stabilo Boss", "Pastel x6.", new BigDecimal("38.00"), null,
                                "img/estabilo boss pastel.webp", "Marca"));
                productos.add(new Producto("Cuaderno Star Wars", "Darth Vader.", new BigDecimal("26.50"), null,
                                "img/cuaderno star wars.webp", "Marca"));
                productos.add(new Producto("Faber Artist Pens", "Pitt.", new BigDecimal("75.00"), null,
                                "img/faber castell pitt artist.webp", "Marca"));
                productos.add(new Producto("Lonchera Princesas", "Disney.", new BigDecimal("52.00"), null,
                                "img/lonchera disney princesas.webp", "Marca"));
                productos.add(new Producto("Taza Batman", "Cerámica.", new BigDecimal("39.90"), null,
                                "img/taza ceramica batman.jpg", "Marca"));
                productos.add(new Producto("Pilot Frixion", "Clicker x3.", new BigDecimal("25.00"), null,
                                "img/Pilot Frixion Clicker x3.jpg", "Marca"));
                productos.add(new Producto("Peluche Pikachu", "Pokémon 25cm.", new BigDecimal("79.90"), null,
                                "img/peluche pikachu.png", "Marca"));
                productos.add(new Producto("Cartas UNO", "Juego mesa.", new BigDecimal("29.90"), null,
                                "img/juego de cartas UNO.webp", "Marca"));
                productos.add(new Producto("Libreta Moleskine", "Clásica.", new BigDecimal("85.00"), null,
                                "img/Libreta Moleskine Clásica.webp", "Marca"));
                productos.add(new Producto("Botella Klean Kanteen", "Térmica.", new BigDecimal("110.00"), null,
                                "img/Botella Klean Kanteen.webp", "Marca"));
                productos.add(new Producto("Colores Polychromos", "Faber-Castell.", new BigDecimal("150.00"), null,
                                "img/COLORES POLYCROMOS.webp", "Marca"));
                productos.add(new Producto("Set Paw Patrol", "Escolar.", new BigDecimal("78.00"), null,
                                "img/SET ESCOLAR PAW PATROL.webp", "Marca"));
                productos.add(new Producto("Pintura Liquitex", "Acrílica.", new BigDecimal("18.00"), null,
                                "img/pintura acrilica liquitex.webp", "Marca"));
                productos.add(new Producto("Taza Friends", "Central Perk.", new BigDecimal("42.00"), null,
                                "img/taza friends central perk.webp", "Marca"));
                productos.add(new Producto("Block Canson", "Acuarela.", new BigDecimal("35.00"), null,
                                "img/block canson acuarela.webp", "Marca"));
                productos.add(new Producto("LEGO", "Construcción.", new BigDecimal("99.00"), null,
                                "img/juego de construccion lego.webp", "Marca"));
                productos.add(new Producto("Mochila Kuromi", "Sanrio.", new BigDecimal("125.00"), null,
                                "img/mochila kuromi sanrio.webp", "Marca"));
                productos.add(new Producto("Portaminas Pentel", "GraphGear.", new BigDecimal("45.00"), null,
                                "img/portaminas pentel.webp", "Marca"));
                productos.add(new Producto("Funko Spider-Man", "Marvel.", new BigDecimal("75.00"), null,
                                "img/funkoPop spiderman.jpeg", "Marca"));
                productos.add(new Producto("Lápices Staedtler", "Grafito.", new BigDecimal("24.00"), null,
                                "img/Lapices Staedtler x12.webp", "Marca"));
                productos.add(new Producto("Rocketbook", "Inteligente.", new BigDecimal("150.00"), null,
                                "img/Rocketbook Inteligente.webp", "Marca"));
                productos.add(new Producto("Saga Harry Potter", "Libros.", new BigDecimal("450.00"), null,
                                "img/Saga Completa Harry Potter.webp", "Marca"));
                productos.add(new Producto("Set Artesco", "Escritorio.", new BigDecimal("42.00"), null,
                                "img/Artesco Set Escritorio.webp", "Marca"));
                productos.add(new Producto("Termo Contigo", "Original.", new BigDecimal("95.00"), null,
                                "img/termo contigo.webp", "Marca"));
                productos.add(new Producto("Taza Minions", "Viaje.", new BigDecimal("38.00"), null,
                                "img/taza de viaje minions.webp", "Marca"));
                productos.add(new Producto("Sharpie x24", "Marcadores.", new BigDecimal("68.00"), null,
                                "img/Marcadores Sharpie x24.webp", "Marca"));
                productos.add(new Producto("Figura Dragon Ball", "Acción.", new BigDecimal("55.00"), null,
                                "img/FIGURA DRAGON ARMAS BALL.jpeg", "Marca"));

                productoRepo.saveAll(productos);
                System.out.println(">>> " + productos.size()
                                + " productos KOKY cargados exitosamente en la base de datos.");
        }
}
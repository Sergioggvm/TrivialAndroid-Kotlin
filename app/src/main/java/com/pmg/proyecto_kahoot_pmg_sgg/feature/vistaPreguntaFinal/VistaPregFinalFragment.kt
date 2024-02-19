package com.pmg.proyecto_kahoot_pmg_sgg.feature.vistaPreguntaFinal

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pmg.proyecto_kahoot_pmg_sgg.R
import com.pmg.proyecto_kahoot_pmg_sgg.core.common.ConstantesNavegacion
import com.pmg.proyecto_kahoot_pmg_sgg.core.domain.model.jugador.InformacionTablero
import kotlin.properties.Delegates

// Author: Pablo Mata

class VistaPregFinalFragment : Fragment() {

    private val args: VistaPregFinalFragmentArgs by navArgs()

    private val viewModel: VistaPregFinalViewModel by viewModels()
    private lateinit var preguntaTextView: TextView

    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    /**
     * Representa el ID del jugador activo en el juego. Utilizado para identificar al jugador cuyo turno es actual.
     * La propiedad es manejada por la delegación de 'Delegates.notNull()' para garantizar que siempre tenga un valor no nulo.
     */
    private var jugadorActivo by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_vista_pregfinal, container, false)

        // Inicializar las vistas directamente
        preguntaTextView = view.findViewById(R.id.tv_mostrarDatos)

        btn0= view.findViewById(R.id.btnRespuesta1)
        btn1 = view.findViewById(R.id.btnRespuesta2)
        btn2 = view.findViewById(R.id.btnRespuesta3)
        btn3 = view.findViewById(R.id.btnRespuesta4)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Llama al método onCreate del ViewModel al crear la vista
        viewModel.onCreate()

        jugadorActivo = args.Jugador

        btn0.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(0, btn0)
        }

        btn1.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(1, btn1)
        }

        btn2.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(2, btn2)
        }

        btn3.setOnClickListener{

            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false

            viewModel.comprobarRespuestaAcertada(3, btn3)
        }

        // Observa los cambios en el LiveData del ViewModel y actualiza la interfaz de usuario
        viewModel.repasoModel.observe(viewLifecycleOwner) { pregunta ->

            // Actualiza la interfaz de usuario con la nueva pregunta
            preguntaTextView.text = pregunta?.oracion

            // Actualiza los botones con las nuevas respuestas
            btn0.text = pregunta?.respuestas?.get(0).toString()
            btn1.text = pregunta?.respuestas?.get(1).toString()
            btn2.text = pregunta?.respuestas?.get(2).toString()
            btn3.text = pregunta?.respuestas?.get(3).toString()

            btn0.isEnabled = true
            btn1.isEnabled = true
            btn2.isEnabled = true
            btn3.isEnabled = true

        }

        // Observa los cambios en el LiveData de shouldNavigateBack del viewModel. Si el valor es true, navega hacia atrás
        viewModel.juegoGanado.observe(viewLifecycleOwner) { juegoGanado ->
            if (juegoGanado) {
                ganarJuego()
            }
        }

        // Observa los cambios en el LiveData de shouldShowDialog del viewModel. Si el valor es true, muestra un diálogo
        viewModel.juegoPerdido.observe(viewLifecycleOwner) { juegoPerdido ->
            if (juegoPerdido) {
                alertaDerrota()
            }
        }

        // Agrega el OnBackPressedCallback al fragmento para evitar que se cierre la aplicación al pulsar el botón "Atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hace nada
            }
        })

    }


    /**
     * Función llamada cuando el jugador gana el juego de la Pregunta Final.
     * Realiza acciones adicionales y navega de regreso al fragmento anterior.
     */
    private fun ganarJuego() {
        // Realizar acciones adicionales cuando se gana el juego

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                resultadoPruebaFinal = true,
                cambioJugador = false
            )
            )
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }

    /**
     * Función llamada cuando el jugador pierde el juego de la Pregunta Final.
     * Realiza acciones adicionales, como navegar hacia atrás y comunicar el resultado y el cambio de jugador.
     * La información se comunica al fragmento anterior a través de la entrada manejada por el estado guardado.
     */
    private fun perderJuego() {
        // Realizar acciones adicionales cuando se pierde el juego
        // Por ejemplo, navegar hacia atrás

        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(
                ConstantesNavegacion.infoTableroKey, InformacionTablero(
                jugador = jugadorActivo,
                    resultadoPruebaFinal = false,
                cambioJugador = true
            )
            )
        }

        findNavController().popBackStack(R.id.vistaTableroView, false)
    }


    /**
     * Función llamada cuando el jugador pierde el juego de la Pregunta Final.
     * Realiza acciones adicionales y navega de regreso al fragmento anterior.
     */
    private fun alertaDerrota() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.alerta_derrota_titulo))
        builder.setMessage(getString(R.string.alerta_derrota_mensaje))
        builder.setPositiveButton(getString(R.string.boton_aceptar)) { _, _ ->
            perderJuego()
        }
        builder.show()
    }
}
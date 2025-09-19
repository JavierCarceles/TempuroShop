package com.tempuro.auth.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenTelemetryConfig {

    private static final String SERVICE_NAME = "auth-service";
    private static final String OTLP_ENDPOINT = "http://localhost:4317"; // Collector OTLP

    private static final Logger logger = LoggerFactory.getLogger(OpenTelemetryConfig.class);

    /*
     * 1. Creamos un exportador OTLP que enviará las trazas a Jaeger o Grafana.
     * 2. Creamos un BatchSpanProcessor para enviar los spans en lotes, con límite de tamaño y retraso configurables.
     * 3. Creamos un Resource con metadatos del servicio (ej: service.name).
     * 4. Creamos un SdkTracerProvider, agregando el BatchSpanProcessor y el Resource.
     * 5. Creamos y registramos un OpenTelemetrySdk global para que cualquier clase pueda usarlo.
     * 6. Obtenemos un Tracer global que se usará para crear spans en el código.
     */
    @PostConstruct
    public void initOpenTelemetry() {

        /*
         * Exportador OTLP: responsable de enviar los spans al collector.
         * Builder nos permite configurar el endpoint antes de crear la instancia final.
         */
        OtlpGrpcSpanExporter otlpExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(OTLP_ENDPOINT)
                .build();

        /*
         * BatchSpanProcessor: gestiona cómo se envían los spans.
         * setScheduleDelay: tiempo de espera entre envíos.
         * setMaxQueueSize: máximo número de spans en cola.
         * setMaxExportBatchSize: máximo número de spans por lote.
         */
        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(otlpExporter)
                .setScheduleDelay(Duration.ofMillis(1000))
                .setMaxQueueSize(2048)
                .setMaxExportBatchSize(512)
                .build();

        /*
         * Resource: define metadatos del servicio.
         * Attributes.builder().put(): permite añadir pares clave-valor (ej: service.name).
         * Resource.create(): crea el objeto Resource final con los atributos.
         */
        Resource serviceResource = Resource.create(
                Attributes.builder()
                        .put("service.name", SERVICE_NAME)
                        .build()
        );

        /*
         * SdkTracerProvider: motor que gestiona la creación y exportación de spans.
         * addSpanProcessor: agregamos el BatchSpanProcessor configurado.
         * setResource: asociamos los metadatos del servicio.
         */
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(serviceResource)
                .build();

        /*
         * OpenTelemetrySdk global: registro del SDK para que cualquier clase pueda obtener el Tracer.
         * setTracerProvider: asociamos nuestro TracerProvider.
         * buildAndRegisterGlobal(): construye el SDK y lo registra globalmente.
         */
        OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();

        /*
         * Tracer global: objeto que usaremos para crear spans en cualquier parte del servicio.
         * GlobalOpenTelemetry.getTracer(): obtiene un Tracer por nombre de servicio.
         */
        GlobalOpenTelemetry.getTracer(SERVICE_NAME);

        /*
         * Mensaje de confirmación de inicialización usando Logger
         */
        logger.info("✅ OpenTelemetry inicializado para servicio: {}", SERVICE_NAME);
    }
}

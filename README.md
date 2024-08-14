# Kafka Reactive

1. Dependências
   Certifique-se de ter as seguintes dependências no seu arquivo pom.xml (para Maven):
```yaml
<dependencies>
    
    <!-- Spring Kafka -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    
    <!-- Reactor Kafka -->
    <dependency>
        <groupId>io.projectreactor.kafka</groupId>
        <artifactId>reactor-kafka</artifactId>
    </dependency>
    
    <!-- Outros se necessário -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>

```
2. Configuração do Kafka Reativo
   Você precisa configurar o ReceiverOptions que será usado para criar o listener reativo.
```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaReactiveConfig {

    @Bean
    public ReceiverOptions<String, String> kafkaReceiverOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return ReceiverOptions.create(props);
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> receiverOptions) {
        return KafkaReceiver.create(receiverOptions.subscription("my-topic"));
    }
}

```

3. Implementação do Listener Reativo
   Agora você pode implementar um listener que processa mensagens de forma reativa.
```java
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class KafkaReactiveListener {

    public KafkaReactiveListener(KafkaReceiver<String, String> kafkaReceiver) {
        Flux<ReceiverRecord<String, String>> kafkaFlux = kafkaReceiver.receive();

        kafkaFlux
            .doOnNext(record -> {
                // Processa a mensagem aqui
                System.out.println("Recebido: " + record.value());

                // Confirma o offset após o processamento
                record.receiverOffset().acknowledge();
            })
            .doOnError(e -> {
                // Lida com erros
                System.err.println("Erro no Kafka: " + e.getMessage());
            })
            .subscribe();  // Subscrição para iniciar o fluxo
    }
}

```
Explicação
KafkaReceiver: KafkaReceiver é a classe central para criar um listener reativo. Ele é configurado usando o ReceiverOptions.
Flux: As mensagens são consumidas em um Flux, que é um tipo reativo que pode emitir 0 ou mais elementos de forma assíncrona.
doOnNext: Processa cada mensagem recebida. Aqui você pode adicionar sua lógica de negócio.
acknowledge: Confirma o processamento da mensagem, avançando o offset.
doOnError: Lida com possíveis erros durante o processamento.
4. Execução
   Ao iniciar a aplicação Spring, o listener será iniciado automaticamente e começará a consumir mensagens do tópico Kafka especificado.




### Spring WebFlux com ConcurrentHashMap
<b>Para lidar com esse cenário no Spring WebFlux, onde você tem um ConcurrentHashMap recebendo dados de um Kafka e precisa fazer chamadas ao banco de dados, você pode seguir as etapas abaixo:</b>
1. Uso do ConcurrentHashMap:
   O ConcurrentHashMap é thread-safe, o que é ideal para manipulação concorrente de dados em ambientes reativos como o Spring WebFlux. Ele permite leituras e escritas simultâneas sem necessidade de sincronização manual.
2. Integração com Kafka:
   Utilize um consumidor reativo para ler as mensagens do Kafka. Com Spring WebFlux, você pode usar o Spring Cloud Stream ou a integração direta com Kafka, com a Flux para processar mensagens de maneira reativa.
   À medida que as mensagens chegam, você pode armazená-las no ConcurrentHashMap com base em uma chave identificadora.
3. Consulta ao Banco de Dados:
   Quando precisar buscar dados no banco de dados, utilize os operadores reativos (Mono ou Flux) para fazer consultas de forma não bloqueante.
   Por exemplo, se precisar buscar dados com base na chave presente no ConcurrentHashMap, você pode fazer algo assim:

```java
Mono<ResultadoDoBanco> resultado = bancoDeDadosService.buscarPorChave(chave);
resultado.flatMap(resultadoBanco -> {
    // Processar o resultado do banco e salvar no ConcurrentHashMap
    concurrentHashMap.put(chave, resultadoBanco);
    return Mono.just(resultadoBanco);
}).subscribe();

```

4. Fluxo Completo:
   Quando uma mensagem chega do Kafka:
   Armazene-a no ConcurrentHashMap.
   Faça uma chamada ao banco de dados usando o serviço reativo.
   Quando o resultado do banco de dados for retornado, atualize a entrada correspondente no ConcurrentHashMap com as informações completas.
5. Tratamento de Conflitos e Consistência:
   Como diferentes threads podem acessar simultaneamente o ConcurrentHashMap, é importante definir estratégias para tratar a consistência dos dados. Isso pode incluir verificações de existência e atualizações condicionais (putIfAbsent, compute, etc.).
   Exemplo Simplificado:


```java
Flux<MensagemKafka> mensagensKafka = kafkaReceiver.receiveMessages();

mensagensKafka.flatMap(mensagem -> {
    String chave = mensagem.getChave();
    concurrentHashMap.put(chave, mensagem);

    return bancoDeDadosService.buscarPorChave(chave)
            .doOnNext(resultado -> concurrentHashMap.put(chave, resultado))
            .then();
}).subscribe();

```

Neste exemplo:

O ConcurrentHashMap é atualizado com a mensagem inicial do Kafka.
Depois, uma consulta ao banco de dados é feita de forma não bloqueante.
O resultado é utilizado para atualizar o ConcurrentHashMap.

1. Configuração do Kafka com Avro:
   Primeiro, configure os deserializers para as chaves e valores das mensagens Kafka. No caso do Avro, isso envolve configurar os KafkaAvroDeserializer para a chave (keyDeserializer) e o valor (valueDeserializer).
   Exemplo de Configuração: 


   <b>application.yml ou application.properties:</b>

```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: grupo-exemplo
      key-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
      properties:
        schema.registry.url: http://localhost:8081
    properties:
      specific.avro.reader: true

```
2. Modelo Avro:
   Certifique-se de que você tem os schemas Avro definidos para a chave e o valor.
   Gere as classes Java usando o Avro maven-plugin ou outra ferramenta de sua preferência.
3. Consumindo Mensagens com WebFlux:
   Utilizando o KafkaReceiver (ou ReactiveKafkaConsumerTemplate no caso do Spring Kafka Reactive), você pode consumir mensagens de forma reativa e processá-las.
4. Processamento e Armazenamento no ConcurrentHashMap:
   Após deserializar as mensagens, você pode armazená-las no ConcurrentHashMap e fazer chamadas ao banco de dados conforme necessário.
   Exemplo de Código:

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaConsumerService {

    private final ConcurrentHashMap<String, SeuObjetoAvro> concurrentHashMap = new ConcurrentHashMap<>();

    @Autowired
    private ReactiveKafkaConsumerTemplate<String, SeuObjetoAvro> kafkaConsumerTemplate;

    @Autowired
    private BancoDeDadosService bancoDeDadosService;

    public KafkaConsumerService() {
        kafkaConsumerTemplate.receiveAutoAck()
                .flatMap(consumerRecord -> {
                    String chave = consumerRecord.key();
                    SeuObjetoAvro valor = consumerRecord.value();

                    // Armazenar a mensagem no ConcurrentHashMap
                    concurrentHashMap.put(chave, valor);

                    // Fazer uma chamada ao banco de dados com base na chave
                    return bancoDeDadosService.buscarPorChave(chave)
                            .doOnNext(resultado -> {
                                // Atualizar ConcurrentHashMap com o resultado do banco
                                concurrentHashMap.put(chave, resultado);
                            })
                            .then();
                })
                .subscribe();
    }
}

```

### Exemplo do request
```json
curl --location 'localhost:8080/api/kafka/send' \
--header 'Content-Type: application/json' \
--data-raw '{
  "CD_TRAN_RECB": "TRX123",
  "CD_QRCD_RECB": "QR001",
  "TP_PESS_RECD": "F",
  "NR_CPF_CNPJ_RECD": 12345678901,
  "NM_RECD": "John Doe",
  "CD_AGEN_RECD": 1234,
  "CD_CNTA_RECD": "AC123456",
  "TP_CNTA_RECD": "CC",
  "CD_ENDE_CADR_BACEN": "pix@example.com",
  "CD_FORM_INIC_PGTO": "ATM",
  "DT_VENC_QRCD": "2023-08-10",
  "DH_ENVI_RECB_BACEN": "2023-08-10T12:30:45.123456",
  "VL_ORIG_QRCD": 100.00,
  "VL_PGTO_INTT": 100.00,
  "TP_PESS_PGAD_ORIG": "F",
  "NR_CPF_CNPJ_ORIG": 98765432100,
  "TP_PESS_PGAD": "F",
  "NR_CPF_CNPJ_PGAD": 11223344556,
  "NM_PGAD": "Jane Doe",
  "TX_INFO_COMP_QRCD": "Payment for invoice 123",
  "CD_SITU_OPER": 1,
  "CD_MOED_PGTO_INTT": "BRL"
}
'
```
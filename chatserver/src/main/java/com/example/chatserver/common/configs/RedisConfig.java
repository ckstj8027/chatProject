package com.example.chatserver.common.configs;

import com.example.chatserver.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    // 연결 기본 객체
    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        //redis pub/sub 에서는 특정 데이터 베이스에 의존적이지않음
        // configuration.setDatabase(0); 레디스에는 여러  db 가 있고 0번은 로그인 db 1번은 캐싱 db 이런식으로 사용할수있다
        // 여기서는 큰 의미는 없다
        return new LettuceConnectionFactory(configuration);


    }

    //publish 객체
    @Bean
    @Qualifier("chatPubSub")
    // 일반적으로 RedisTemplate<key 데이터타입, value 데이터타입>을 사용
    // 근데 여기서는 우리는 key value 형식으로 데이터를 저장할려고 쓰는게 아니고
    // 메시지를 전파하고 받는 거에 쓰이기에 StringRedisTemplate 를 사용하는거다
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub")RedisConnectionFactory redisConnectionFactory){
        return new StringRedisTemplate(redisConnectionFactory);
    }


    // subscribe 객체
    //Redis의 메시지 리스너 컨테이너로, 이 객체는 Redis 채널에 대한 구독을 처리하고,
    // 해당 채널에서 발생하는 메시지를 리스닝(감시)합니다. 메시지가 들어오면
    // MessageListener를 통해 메시지를 처리할 수 있습니다.
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatPubSub")RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter messageListenerAdapter

    ){

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter,new PatternTopic("chat"));
//        이 메서드는 메시지 리스너를 Redis 채널에 등록하는 역할을 합니다.
//         첫 번째 매개변수인 messageListenerAdapter는 메시지를 처리할 리스너 객체입니다.
//         두 번째 매개변수인 new PatternTopic("chat")는 메시지를 구독할 채널 이름 또는 주제를 지정합니다.
//         여기서는 "chat"라는 주제를 구독하게 되며, 해당 주제에서 메시지가 발행되면 messageListenerAdapter가 이를 처리합니다.
//        PatternTopic은 주제를 지정할 때 패턴을 사용해서 구독할 수 있게 합니다. 이 예제에서는 "chat"라는 하나의 채널을 구독하고 있습니다.

        return container;

    }





    //redis 에서 수신되는 메시지를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService){
        // RedisPubSubService 의 특정 메서드가 수신된 메시지를 처리할수 있도록 지정하는
        return new MessageListenerAdapter(redisPubSubService,"onMessage");

    }

}

# springRedisCache

# For Lettuce connection

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisServerConfiguration {

    @Autowired
    private EnvParser envParser;

    @Value("${spring.redis.pool.max-active: 1000}")
    private int REDIS_POOL_MAX_ACTIVE;

    @Value("${spring.redis.pool.max-idle: 100}")
    private int REDIS_POOL_MAX_IDLE;

    @Value("${spring.redis.pool.min-idle: 20}")
    private int REDIS_POOL_MIN_IDLE;

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration(envParser.getHostName(), envParser.getPort());
    }

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    LettucePoolingClientConfiguration lettucePoolConfig(ClientOptions options, ClientResources dcr){
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(new GenericObjectPoolConfig())
                .clientOptions(options)
                .clientResources(dcr)
                .build();
    }

    @Bean
    public RedisConnectionFactory connectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                    LettucePoolingClientConfiguration lettucePoolConfig) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
    }

    @Bean
    public ClientOptions clientOptions(){
        return ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .build();
    }


    @Bean
    RedisTemplate<String, ActivityTrackerNode> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ActivityTrackerNode> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}

# Jedis Connection


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(envParser.getHostName());
        redisStandaloneConfiguration.setPort(envParser.getPort());
        redisStandaloneConfiguration.setPassword(envParser.getPassword());

        
        JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration);
        factory.getUsePool();
        JedisPoolConfig poolConfig = (JedisPoolConfig) factory.getPoolConfig();
        poolConfig.setMaxTotal(REDIS_POOL_MAX_ACTIVE);
        poolConfig.setMinIdle(REDIS_POOL_MIN_IDLE);
        poolConfig.setMaxIdle(REDIS_POOL_MAX_IDLE);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        return factory;
    }

    @Bean
    RedisTemplate<String, ActivityTrackerNode> redisTemplate() {
        RedisTemplate<String, ActivityTrackerNode> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

package ait.cohort70.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.config.Configuration.AccessLevel;

@Configuration
public class ServiceConfiguration {

    @Bean // бин ModelMapper для использования в приложении через внедрение зависимостей (@Autowired) в другие классы Spring контекста
    ModelMapper getModelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true) // разрешаем маппинг по полям (иначе по геттерам/сеттерам)
                .setFieldAccessLevel(AccessLevel.PRIVATE) // разрешаем доступ к приватным полям
                .setMatchingStrategy(MatchingStrategies.STRICT); // строгая стратегия маппинга (только точно совпадающие имена полей)
        return mapper;
    }
}

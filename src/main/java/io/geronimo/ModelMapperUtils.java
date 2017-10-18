package io.geronimo;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.modelmapper.Conditions;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Slf4j
public class ModelMapperUtils {

    private static ModelMapper modelMapper;

    private static ModelMapper newModelMapperInstance() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        /*modelMapper.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PACKAGE_PRIVATE);
        modelMapper.getConfiguration().setMethodAccessLevel(Configuration.AccessLevel.PACKAGE_PRIVATE);*/

        return modelMapper;
    }

    public static ModelMapper getModelMapper() {

        if (modelMapper == null) {
            modelMapper = newModelMapperInstance();
        }

        return modelMapper;
    }

    public static <T> T map(Object source, Class<T> destinationClass) throws MappingException {
        return getModelMapper().map(source, destinationClass);
    }

    private static MapperFactory mapperFactory;

    private static MapperFactory newMapperFactoryInstance() {
        return new DefaultMapperFactory.Builder().build();
    }

    public static MapperFactory getMapperFactory() {
        if (mapperFactory == null) {
            mapperFactory = newMapperFactoryInstance();
        }

        return mapperFactory;
    }

    public static <Src, Dest> Dest map(Src src, Class<Dest> destClass, String mapperName) {

        Type<Src> srcType = new TypeBuilder<Src>() {}.build();
        Type<Dest> destType = new TypeBuilder<Dest>() {}.build();

        MapperFactory mapperFactory = getMapperFactory();

        mapperFactory.classMap(srcType, destClass).byDefault()
                .customize(AppContextManager.getBean(mapperName, Mapper.class)).register();

        return mapperFactory.getMapperFacade(srcType, destType).map(src);
    }

}


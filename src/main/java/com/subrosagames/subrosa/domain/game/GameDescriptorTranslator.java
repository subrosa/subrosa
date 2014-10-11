package com.subrosagames.subrosa.domain.game;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.domain.game.persistence.RequiredAttributeEntity;
import com.subrosagames.subrosa.util.bean.OptionalAwareSimplePropertyCopier;

/**
 * Handles the translation of a game descriptor into a game entity.
 */
public final class GameDescriptorTranslator {

    private GameDescriptorTranslator() {
    }

    /**
     * Copies game data into the game entity, performing entity translations in the process.
     *
     * @param game           game entity
     * @param gameDescriptor game descriptor
     */
    public static void ingest(BaseGame game, GameDescriptor gameDescriptor) {
        copyProperties(gameDescriptor, game);

        game.getRequiredAttributes().clear();
        Optional<Set<String>> attributes = gameDescriptor.getRequiredAttributes();
        if (attributes == null) {
            Set<String> emptySet = Sets.newHashSet();
            attributes = Optional.of(emptySet);
        }
        for (String attribute : attributes.or(Sets.<String>newHashSet())) {
            RequiredAttributeEntity attributeEntity = new RequiredAttributeEntity();
            attributeEntity.setGame(game);
            attributeEntity.setName(attribute);
            game.addRequiredAttribute(attributeEntity);
        }
    }

    private static void copyProperties(Object dto, Object entity) {
        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(entity, dto);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}

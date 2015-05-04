package com.subrosagames.subrosa.domain.game;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.dto.EnrollmentFieldDto;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.domain.game.persistence.EnrollmentFieldEntity;
import com.subrosagames.subrosa.domain.game.persistence.EnrollmentFieldPk;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
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
     * @throws ImageNotFoundException if specified image does not exist
     */
    public static void ingest(BaseGame game, GameDescriptor gameDescriptor) throws ImageNotFoundException {
        copyProperties(gameDescriptor, game);

        game.getPlayerInfo().clear();
        Optional<List<EnrollmentFieldDto>> enrollmentFields = gameDescriptor.getPlayerInfo();
        if (enrollmentFields == null) {
            List<EnrollmentFieldDto> emptyList = Lists.newArrayList();
            enrollmentFields = Optional.of(emptyList);
        }
        for (EnrollmentField field : enrollmentFields.or(Lists.<EnrollmentFieldDto>newArrayList())) {
            EnrollmentFieldEntity entity = new EnrollmentFieldEntity();
            entity.setPrimaryKey(new EnrollmentFieldPk(game.getId(), RandomStringUtils.randomAlphabetic(7)));
            entity.setGame(game);
            entity.setName(field.getName());
            entity.setDescription(field.getDescription());
            entity.setType(field.getType());
            game.addEnrollmentField(entity);
        }

        if (gameDescriptor.getImageId() != null) {
            if (gameDescriptor.getImageId().isPresent()) {
                Image image = game.getOwner().getImage(gameDescriptor.getImageId().get());
                game.setImage(image);
            } else {
                game.setImage(null);
            }
        }
    }

    private static void copyProperties(Object dto, Object entity) {
        OptionalAwareSimplePropertyCopier beanCopier = new OptionalAwareSimplePropertyCopier();
        try {
            beanCopier.copyProperties(entity, dto);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}

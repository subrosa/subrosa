package com.subrosagames.subrosa.domain.game;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.location.Coordinates;

/**
 * Repository for retrieval of game information.
 */
public interface GameRepository extends DomainObjectRepository<BaseGame, Integer> {

    /**
     * Get the game for the provided url.
     *
     * @param url        game url
     * @param expansions fields to expand
     * @return game
     */
    default Optional<BaseGame> findOneByUrl(String url, String... expansions) {
        return findOne((root, query, cb) -> cb.equal(root.get("url"), url), expansions);
    }

    /**
     * Get games owned by provided user.
     *
     * @param user owning account
     * @return list of games
     */
    List<BaseGame> findAllByOwner(Account user);

    /**
     * Get a list of the games that are occurring near the provided geographical location.
     *
     * @param location   location
     * @param pageable   pagination information
     * @param expansions fields to expand
     * @return games near that location
     */
    default Page<BaseGame> findNearLocation(Coordinates location, Pageable pageable, String... expansions) {
        String jpql = "SELECT g "
                + "FROM BaseGame g "
                + "ORDER BY 3959 * acos( "
                + "   cos(radians(:latitude)) * cos(radians(g.location.latitude)) "
                + "       * cos(radians(g.location.longitude) - radians(:longitude)) "
                + "       + sin(radians(:latitude)) * sin(radians(g.location.latitude)) ) ASC";
        TypedQuery<BaseGame> query = getEntityManager().createQuery(jpql, BaseGame.class);
        query.setParameter("latitude", location.getLatitude());
        query.setParameter("longitude", location.getLongitude());
        applyHints(query, Arrays.stream(expansions).map(EntityGraphHint::loadGraphName).toArray(EntityGraphHint[]::new));
        return pageable == null
                ? new PageImpl<>(query.getResultList())
                : readPage(query, pageable, null);
    }

}

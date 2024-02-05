package repo

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters
import entity.TextbookMapping
import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.core.Vertx
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import mutiny.zero.flow.adapters.AdaptersToFlow



/**
@author Yu-Jing
@create 2024-02-04-下午 03:58
 */

/**
 * bug:
 * ReactiveMongoClient
 * https://github.com/quarkusio/quarkus/issues/36177
 */
@ApplicationScoped
class TextbookMappingRepo @Inject constructor(
    private val mongoClient: MongoClient,
    private val reactiveMongoClient: ReactiveMongoClient,
    private val vertx: Vertx
){

    private val context = vertx.getOrCreateContext()
    private val contextId = context.deploymentID()


    private val collection  = mongoClient
        .getDatabase("nu_ehanlin")
        .getCollection("TextbookMapping", TextbookMapping::class.java)

    private val reactiveCollection  = reactiveMongoClient
        .getDatabase("nu_ehanlin")
        .getCollection("TextbookMapping", TextbookMapping::class.java)

    private val unwrapReactiveCollection = reactiveMongoClient.unwrap()
        .getDatabase("nu_ehanlin")
        .getCollection("TextbookMapping", TextbookMapping::class.java)


    /**
     * original Reactive
     */
    fun reactiveFindSubject(subjects: List<String>): Uni<List<TextbookMapping>>? {
        return reactiveCollection
            .find(Filters.`in`("subject", subjects))
            .collect()
            .asList()
    }

    fun reactiveFindSubject(subject: String): Uni<List<TextbookMapping>>? {
        return reactiveCollection
            .find(Filters.eq("subject", subject))
            .collect()
            .asList()
    }

    /**
     * modified Reactive
     */
    fun improveReactiveFindSubject(subjects: List<String>): Uni<List<TextbookMapping>>? {
        val query = unwrapReactiveCollection.find(Filters.`in`("subject", subjects))

        return Multi.createFrom()
            .publisher(AdaptersToFlow.publisher(query))
            .runSubscriptionOn { cmd -> context.runOnContext { cmd.run() } }
            .collect()
            .asList()
    }

    fun improveReactiveFindSubject(subject: String): Uni<List<TextbookMapping>>? {
        val query = unwrapReactiveCollection.find(Filters.eq("subject", subject))
        return Multi.createFrom()
            .publisher(AdaptersToFlow.publisher(query))
            .collect()
            .asList()
    }


    /**
     * non-reactive
     */
    fun nonReactiveFindSubject(subjects: List<String>): List<TextbookMapping> {
        return collection
            .find(Filters.`in`("subject", subjects))
            .toList()

    }

    fun nonReactiveFindSubject(subject: String): List<TextbookMapping> {
        return collection
            .find(Filters.eq("subject", subject))
            .toList()


    }
}
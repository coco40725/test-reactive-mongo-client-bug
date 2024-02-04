package ctrl

import entity.TextbookMapping
import io.smallrye.common.annotation.NonBlocking
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import repo.TextbookMappingRepo

/**
@author Yu-Jing
@create 2024-02-04-下午 04:14
 */
@Path("/textbook-mapping")
class TextbookMappingCtrl @Inject constructor(
    private val textbookMappingRepo: TextbookMappingRepo
) {

    private val subjects = listOf("EMA", "HCW", "JBI", "JCT", "JEN", "JGE", "JHI", "JMA", "JPC")

    /**
     * time: 7s
     */
    @Path("/reactive/in")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    fun reactiveFindSubjectIn(): Uni<List<TextbookMapping>>? {
        return textbookMappingRepo.reactiveFindSubject(subjects)
    }


    /**
     * time: 1519ms
     */
    @Path("/reactive")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    fun reactiveFindSubject(): Uni<List<TextbookMapping>>? {
        val unis = subjects.map { subject ->
            textbookMappingRepo.reactiveFindSubject(subject)
        }

        return Uni.join().all(unis).andCollectFailures().map { list ->
            val result = list.flatten()
            println("result: $result")
            result
        }
    }

    /**
     * time: 343ms
     */
    @Path("/improve/reactive/in")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    fun improveReactiveFindSubjectIn(): Uni<List<TextbookMapping>>? {
        return textbookMappingRepo.improveReactiveFindSubject(subjects)
            ?.map { list ->
                println("list: ${list.size}")
                list
            }
    }


    /**
     * time: 700ms
     */
    @Path("/improve/reactive")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    fun improveReactiveFindSubject(): Uni<List<TextbookMapping>>? {
        val unis = subjects.map { subject ->
            textbookMappingRepo.improveReactiveFindSubject(subject)
        }

        return Uni.join().all(unis).andCollectFailures().map { list ->
            list.flatten()
        }
    }

    /**
     * time: 358ms
     */
    @Path("/non-reactive/in")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun nonReactiveFindSubjectIn(): List<TextbookMapping>? {
        return textbookMappingRepo.nonReactiveFindSubject(subjects)
    }


    /**
     * time: 653ms
     */
    @Path("/non-reactive")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun nonReactiveFindSubject(): List<TextbookMapping?> {
        val list =subjects.map { subject ->
            textbookMappingRepo.nonReactiveFindSubject(subject)
        }
       return list.flatten()
    }



}
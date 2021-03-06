package com.popstack.mvoter2015.domain.candidate.usecase

import com.popstack.mvoter2015.domain.CoroutineUseCase
import com.popstack.mvoter2015.domain.DispatcherProvider
import com.popstack.mvoter2015.domain.candidate.CandidateRepository
import com.popstack.mvoter2015.domain.candidate.model.Candidate
import com.popstack.mvoter2015.domain.exception.NetworkException
import com.popstack.mvoter2015.domain.location.LocationRepository
import javax.inject.Inject

class GetMyLowerHouseCandidateList @Inject constructor(
  dispatcherProvider: DispatcherProvider,
  private val candidateRepository: CandidateRepository,
  private val locationRepository: LocationRepository
) :
  CoroutineUseCase<Unit, List<Candidate>>(dispatcherProvider) {

  private var hasRetriedWithWardUpdate = false

  override suspend fun provide(input: Unit): List<Candidate> {

    return try {
      candidateRepository.getCandidateList(
        locationRepository.getUserWard()!!.lowerHouseConstituency.id
      )
    } catch (networkException: NetworkException) {
      if (networkException.errorCode == 404 && hasRetriedWithWardUpdate.not()) {
        //Try updating ward because constituency id might have been updated with new constituency ids
        hasRetriedWithWardUpdate = true

        val userWard = locationRepository.getUserWard()!!
        val userStateRegionTownship = locationRepository.getUserStateRegionTownship()

        val wardDetails = locationRepository.getWardDetails(
          stateRegion = userStateRegionTownship?.stateRegion!!,
          townshipIdentifier = userStateRegionTownship.township,
          wardName = userWard.name
        )

        locationRepository.saveUserWard(wardDetails)

        return candidateRepository.getCandidateList(
          locationRepository.getUserWard()!!.lowerHouseConstituency.id
        )
      } else {
        throw networkException
      }
    }

  }

}
package com.popstack.mvoter2015.domain.candidate

import com.popstack.mvoter2015.domain.candidate.model.Candidate
import com.popstack.mvoter2015.domain.candidate.model.CandidateId
import com.popstack.mvoter2015.domain.constituency.model.ConstituencyId

interface CandidateRepository {

  suspend fun getCandidateList(constituencyId: ConstituencyId): List<Candidate>

  fun getCandidate(candidateId: CandidateId): Candidate

  fun searchCandidate(query: String, pageNo: Int, resultPerPage: Int): List<Candidate>

  fun getRivalCandidatesInConstituency(constituencyId: ConstituencyId): List<Candidate>

}
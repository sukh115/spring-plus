package org.example.expert.domain.manager.service

import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.log.service.LogService
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest
import org.example.expert.domain.manager.dto.response.ManagerResponse
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.manager.repository.ManagerRepository
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils

@Service
@Transactional(readOnly = true)
class ManagerService(
    private val managerRepository: ManagerRepository,
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository,
    private val logService: LogService
) {
    @Transactional
    fun saveManager(authUser: AuthUser, todoId: Long, managerSaveRequest: ManagerSaveRequest): ManagerSaveResponse {
        logService.saveLog("매니저 등록 요청 : 작성자 ID = " + authUser.id + ", 대상 유저 ID = " + managerSaveRequest.managerUserId)

        // 일정을 만든 유저
        val user = User.fromAuthUser(authUser)
        val todo = todoRepository.findById(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        if (!ObjectUtils.nullSafeEquals(user.id, todo.user.id)) {
            throw InvalidRequestException("담당자를 등록하려고 하는 유저가 유효하지 않거나, 일정을 만든 유저가 아닙니다.")
        }

        val managerUser = userRepository.findById(managerSaveRequest.managerUserId)
            .orElseThrow { InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다.") }

        if (ObjectUtils.nullSafeEquals(user.id, managerUser.id)) {
            throw InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.")
        }

        val newManagerUser = Manager(managerUser, todo)
        val savedManagerUser = managerRepository.save(newManagerUser)

        return ManagerSaveResponse(
            savedManagerUser.id!!,
            UserResponse(managerUser.id!!, managerUser.email, user.nickname)
        )
    }

    fun getManagers(todoId: Long): List<ManagerResponse> {
        val todo = todoRepository.findById(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        val managerList = managerRepository.findByTodoIdWithUser(
            todo.id!!
        )

        val dtoList: MutableList<ManagerResponse> = ArrayList()
        for (manager in managerList) {
            val user = manager.user
            dtoList.add(
                ManagerResponse(
                    manager.id!!,
                    UserResponse(user.id!!, user.email, user.nickname)
                )
            )
        }
        return dtoList
    }

    @Transactional
    fun deleteManager(authUser: AuthUser?, todoId: Long, managerId: Long) {
        val user = User.fromAuthUser(
            authUser!!
        )

        val todo = todoRepository.findById(todoId)
            .orElseThrow { InvalidRequestException("Todo not found") }

        if (!ObjectUtils.nullSafeEquals(user.id, todo.user.id)) {
            throw InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.")
        }

        val manager = managerRepository.findById(managerId)
            .orElseThrow { InvalidRequestException("Manager not found") }

        if (!ObjectUtils.nullSafeEquals(todo.id, manager.todo.id)) {
            throw InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.")
        }

        managerRepository.delete(manager)
    }
}

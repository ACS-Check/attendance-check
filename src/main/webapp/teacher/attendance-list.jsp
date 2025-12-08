<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout title="출석 요약">
    <div class="flex flex-col flex-1 w-full px-6 py-8">
        <div class="mb-6 w-full">
            <h1 class="text-2xl font-bold text-gray-900 tracking-tight mb-1">출석 요약</h1>
            <p class="text-gray-600 text-sm">날짜별 출석 인원과 전체 학생 수</p>
            <div class="h-1 w-16 bg-primary-500 mt-2"></div>
        </div>

        <div class="overflow-x-auto rounded-lg shadow-lg">
            <table class="w-full text-sm text-left border border-gray-200 rounded-lg overflow-hidden">
                <thead class="bg-primary-500 text-white">
                    <tr>
                        <th class="px-4 py-3 border-b">날짜</th>
                        <th class="px-4 py-3 border-b text-right">출석 인원</th>
                        <th class="px-4 py-3 border-b text-right">전체 학생 수</th>
                    </tr>
                </thead>
                <tbody>
                                <c:forEach var="row" items="${attendanceSummary}">
                                    <tr class="hover:bg-gray-50">
                                        <td class="px-4 py-3 border-b">${row.date}</td>
                                        <td class="px-4 py-3 border-b text-right">${row.attendanceCount}</td>
                                        <td class="px-4 py-3 border-b text-right">${row.totalStudents}</td>
                                    </tr>
                                </c:forEach>
                    <c:if test="${empty attendanceSummary}">
                        <tr>
                            <td colspan="3" class="px-4 py-8 text-center text-gray-500">데이터가 없습니다.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="mt-6 flex justify-end gap-2">
            <a href="${pageContext.request.contextPath}/teacher/attendanceCode"
                 class="inline-flex items-center rounded-lg bg-primary-500 px-6 py-3 text-white text-base font-bold hover:bg-primary-600 shadow-md hover:shadow-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-300">
                출석 코드 생성
            </a>
        </div>
    </div>
</t:layout>

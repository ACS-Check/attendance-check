<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout title="출석 요약">
    <!-- 제목은 카드 바깥 상단에 배치 -->
    <div class="w-full max-w-4xl mx-auto mb-4">
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">출석 요약</h1>
        <p class="muted">날짜별 출석 인원과 전체 학생 수</p>
    </div>

    <div class="w-full max-w-4xl rounded-md shadow-md p-8 bg-white mx-auto">

    <div class="overflow-x-auto">
            <table class="w-full text-sm text-left border border-gray-200 rounded-md">
                <thead class="bg-gray-50 text-gray-600">
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
                 class="inline-flex items-center rounded-md bg-primary-500 text-white px-4 py-2 font-semibold shadow-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-300">
                출석 코드 생성
            </a>
        </div>
    </div>
</t:layout>

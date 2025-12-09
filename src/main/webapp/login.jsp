<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:layout title="로그인">
  <div class="flex flex-col text-left w-full max-w-[550px] rounded-md shadow-md p-8 bg-white m-auto">
    <div class="mb-6 w-full">
      <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">
        로그인
      </h1>
    </div>

    <div class="w-full">
  <form class="space-y-6" method="post" action="${pageContext.request.contextPath}/login">
        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="userId" class="block text-sm font-semibold text-gray-800">
              아이디
            </label>
          </div>
          <input
            id="userId"
            name="username"
            type="text"
            placeholder="아이디를 입력하세요"
            class="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder-gray-400 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
            required
          />
        </div>

        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="password" class="block text-sm font-semibold text-gray-800">
              비밀번호
            </label>
          </div>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            class="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder-gray-400 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
            required
          />
        </div>

        <div class="flex items-center justify-between">
          <button
            type="submit"
            class="w-full rounded-md bg-primary-600 py-3 text-white font-semibold shadow-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-300"
          >
            로그인
          </button>
          <a
            href="${pageContext.request.contextPath}/register"
            class="ml-4 w-full text-center rounded-md bg-gray-100 py-3 text-gray-700 font-semibold shadow-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-300"
          >
            회원가입
          </a>
        </div>
      </form>
    </div>
  </div>
</t:layout>

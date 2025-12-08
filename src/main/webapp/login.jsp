<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:layout title="통합 LOGIN">
  <div class="flex flex-col text-left w-full max-w-md px-6 py-8">
    <div class="mb-8 w-full">
      <h1 class="text-4xl font-black text-gray-900 tracking-tight">
        통합 LOGIN
      </h1>
      <div class="h-1 w-20 bg-primary-500 rounded-full mt-3"></div>
    </div>

    <div class="w-full">
  <form class="space-y-6" method="post" action="${pageContext.request.contextPath}/login">
        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="userId" class="block text-sm font-bold text-gray-900">
              학번
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
            <label for="password" class="block text-sm font-bold text-gray-900">
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

        <div class="flex items-center justify-between gap-4">
          <button
            type="submit"
            class="w-full rounded-lg bg-primary-500 py-3.5 text-white font-bold shadow-lg hover:bg-primary-600 hover:shadow-xl transform hover:scale-[1.02] transition-all duration-200 focus:outline-none focus:ring-4 focus:ring-primary-200"
          >
            로그인
          </button>
          <a
            href="${pageContext.request.contextPath}/register"
            class="w-full text-center rounded-lg bg-[#232f3e] py-3.5 text-white font-bold hover:bg-[#37475a] transform hover:scale-[1.02] transition-all duration-200 focus:outline-none"
          >
            회원가입
          </a>
        </div>
      </form>
    </div>
  </div>
</t:layout>

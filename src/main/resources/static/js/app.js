const { createApp } = Vue

createApp({
    components: {
        HeaderComp,
        PaginationComp,
        MovieCard
    },
    template: `
    <div class="container">
      <HeaderComp />

      <PaginationComp
        :current="currentPage"
        :total="totalPages"
        :pageSize="pageSize"
        :totalMovies="totalMovies"
        @change="goPage"
      />

      <div v-if="loading" class="loading">⏳ 加载中...</div>
      <div v-if="error" class="error">{{ error }}</div>

      <div class="movie-grid" v-if="!loading">
        <MovieCard
          v-for="(m, i) in movies"
          :key="m.title + i"
          :movie="m"
        />
      </div>
    </div>
  `,
    data() {
        return {
            movies: [],
            loading: false,
            error: null,
            currentPage: 1,
            pageSize: 20,
            totalMovies: 250
        }
    },
    computed: {
        totalPages() {
            return Math.ceil(this.totalMovies / this.pageSize)
        }
    },
    mounted() {
        this.load()
    },
    methods: {
        async load() {
            this.loading = true
            this.error = null
            try {
                const res = await api.getMovies(this.currentPage, this.pageSize)
                this.movies = res.data || []
            } catch (e) {
                this.error = '后端服务异常'
            } finally {
                this.loading = false
            }
        },
        goPage(page) {
            this.currentPage = page
            this.load()
        }
    }
}).mount('#app')
const HeaderComp = {
    template: `
    <div class="header">
      <h1>🎬 豆瓣Top250电影</h1>
      <p>发现经典，品味电影</p>
    </div>
  `
}

const PaginationComp = {
    props: ['current', 'total', 'pageSize', 'totalMovies'],
    emits: ['change'],
    template: `
    <div class="controls">
      <div class="page-info">
        <button @click="change(current - 1)" :disabled="current <= 1">上一页</button>
        <span>第</span>
        <input type="number" :value="current" @change="onInput" />
        <span>页，共 {{ total }} 页</span>
        <button @click="change(current + 1)" :disabled="current >= total">下一页</button>
      </div>
      <div class="total-info">
        每页 {{ pageSize }} 条，共 {{ totalMovies }} 部电影
      </div>
    </div>
  `,
    methods: {
        change(page) {
            if (page >= 1 && page <= this.total) {
                this.$emit('change', page)
            }
        },
        onInput(e) {
            this.change(Number(e.target.value))
        }
    }
}

const MovieCard = {
    props: ['movie'],
    template: `
    <div class="movie-card">
      <img
        class="movie-poster"
        :src="imgUrl"
        referrerpolicy="no-referrer"
        @error="imgError"
      />
      <div class="movie-info">
        <div class="movie-title">{{ movie.title || '未知标题' }}</div>
        <div class="movie-other">{{ movie.other }}</div>
        <div class="movie-rating">
          <span class="rating-score">{{ movie.rating_num || 'N/A' }}</span>
          <span class="rating-stars">{{ stars }}</span>
          <span class="rating-info">({{ movie.voters || 0 }}人评价)</span>
        </div>
        <div class="movie-desc" v-if="movie.desc">{{ movie.desc }}</div>
      </div>
    </div>
  `,
    computed: {
        stars() {
            const r = parseFloat(this.movie.rating5_t)
            if (isNaN(r)) return ''
            const full = Math.floor(r)
            return '★'.repeat(full) + '☆'.repeat(5 - full)
        },
        imgUrl() {
            if (!this.movie.img) {
                return 'https://via.placeholder.com/280x400?text=暂无图片'
            }
            let url = this.movie.img
            if (!url.startsWith('http')) url = 'https:' + url
            return api.proxyImage(url)
        }
    },
    methods: {
        imgError(e) {
            e.target.src = 'https://via.placeholder.com/280x400?text=图片加载失败'
        }
    }
}
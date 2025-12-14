import React from 'react'
import trangChuIcon from '@/assets/trang_chu_icon.png'
import { Link } from 'react-router-dom'



export interface TrangChuPageProps {
  href: string;
  name: string;
  hidden: boolean;
}

export default function TrangChuComponent(props: TrangChuPageProps) {
  if (props.hidden) return null;
  return (
    <Link to={`${props.href}`} className='rounded-2xl hover:scale-105 transition bg-[#0071C6] flex flex-col gap-3 justify-center items-center w-[360px] h-[260px]'>
        <img src={trangChuIcon} alt="trangChuIcon" />
        <div className='text-white font-semibold'>{props.name}</div>
    </Link>
  )
}
